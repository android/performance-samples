#!/usr/bin/env python3
# Copyright 2026 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""Records a Perfetto system trace together with Compose recomposition tracing.

Flow:
  1. Start in-process tracing (ConnectedProfilerTracingReceiver).
  2. Start recomposition tracing (RecompositionTracingReceiver).
  3. Start a Perfetto system trace and wait until CTRL+C / SIGTERM
     (or -t seconds).
  4. Stop recomposition tracing and in-process tracing.
  5. Flush in-process traces and pull them.
  6. Merge the system trace and the in-process traces into a single ZIP that
     can be opened in ui.perfetto.dev / trace_processor.

The target app needs a dependency on androidx.compose.runtime:runtime-tracing
(1.13.0 or newer), which brings in androidx.tracing:tracing-wire.
Requires Android Q (API 29)+ and Python 3.10+.

Example:
  ./record_recomposition_trace.py -p com.example.app -t 10
  ./record_recomposition_trace.py -p com.example.app \\
      -c config.textproto -o out.zip
"""

import argparse
import datetime
import os
import re
import shutil
import signal
import subprocess
import sys
import tempfile
from typing import IO, NoReturn, Optional
import zipfile

PROFILER_RECEIVER = 'androidx.tracing.profiler.ConnectedProfilerTracingReceiver'
PROFILER_ACTION_START = 'androidx.tracing.profiler.action.START'
PROFILER_ACTION_STOP = 'androidx.tracing.profiler.action.STOP'
PROFILER_ACTION_FLUSH = 'androidx.tracing.profiler.action.FLUSH_TRACES_GET_PATH'

RECOMPOSE_RECEIVER = (
    'androidx.compose.runtime.tracing.RecompositionTracingReceiver'
)
RECOMPOSE_ACTION_START = (
    'androidx.compose.tracing.action.START_RECOMPOSE_TRACING'
)
RECOMPOSE_ACTION_STOP = 'androidx.compose.tracing.action.STOP_RECOMPOSE_TRACING'

# ConnectedProfilerTracingReceiver result codes.
PROFILER_RESULT_SUCCESS = 1
PROFILER_RESULT_FLUSH_COMPLETED = 2
PROFILER_RESULT_DELAYED_TRACE_DRIVER = -2

# RecompositionTracingReceiver result codes.
RECOMPOSE_RESULT_SUCCESS = 1
RECOMPOSE_RESULT_ALREADY_IN_PROGRESS = 2

DEVICE_TRACE_DIR = '/data/misc/perfetto-traces/'


def default_config(package: str, buffer_mb: int, duration_s: int) -> str:
    """Returns the default Perfetto textproto config.

    Args:
        package: Application id to enable atrace for.
        buffer_mb: Size of the main ring buffer in MB.
        duration_s: Trace duration in seconds, 0 to trace until stopped.
    """
    duration = f'duration_ms: {duration_s * 1000}' if duration_s > 0 else ''
    return f"""
buffers {{
  size_kb: {buffer_mb * 1024}
  fill_policy: RING_BUFFER
}}
buffers {{
  size_kb: 4096
  fill_policy: RING_BUFFER
}}
data_sources {{
  config {{
    name: "linux.ftrace"
    target_buffer: 0
    ftrace_config {{
      ftrace_events: "sched/sched_switch"
      ftrace_events: "sched/sched_waking"
      ftrace_events: "sched/sched_wakeup_new"
      ftrace_events: "sched/sched_process_exit"
      ftrace_events: "sched/sched_process_free"
      ftrace_events: "task/task_newtask"
      ftrace_events: "task/task_rename"
      ftrace_events: "power/cpu_frequency"
      ftrace_events: "power/cpu_idle"
      ftrace_events: "power/suspend_resume"
      ftrace_events: "ftrace/print"
      atrace_categories: "am"
      atrace_categories: "binder_driver"
      atrace_categories: "dalvik"
      atrace_categories: "freq"
      atrace_categories: "gfx"
      atrace_categories: "input"
      atrace_categories: "res"
      atrace_categories: "sched"
      atrace_categories: "view"
      atrace_categories: "wm"
      atrace_apps: "{package}"
    }}
  }}
}}
data_sources {{
  config {{
    name: "linux.process_stats"
    target_buffer: 1
    process_stats_config {{
      scan_all_processes_on_start: true
    }}
  }}
}}
data_sources {{
  config {{
    name: "android.surfaceflinger.frametimeline"
    target_buffer: 0
  }}
}}
data_sources {{
  config {{
    name: "track_event"
    target_buffer: 0
  }}
}}
{duration}
"""


def build_config(args: argparse.Namespace) -> str:
    """Returns the Perfetto config passed with --config, or the default."""
    if args.config is not None:
        with open(args.config, 'r') as f:
            return f.read()
    return default_config(
        package=args.package, buffer_mb=args.buffer, duration_s=args.time
    )


class ANSI:
    END = '\033[0m'
    BOLD = '\033[1m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    BLACK = '\033[30m'
    BG_YELLOW = '\033[43m'
    BG_BLUE = '\033[44m'


def prt(msg: str, colors: str = ANSI.END) -> None:
    print(colors + msg + ANSI.END, flush=True)


class SignalException(Exception):
    pass


def signal_handler(sig: int, frame: object) -> NoReturn:
    raise SignalException(f'Received signal {sig}')


devnull = open(os.devnull, 'rb')
procs: list[subprocess.Popen] = []


def adb(
    *args: str,
    stdin: Optional[IO | int] = devnull,
    stdout: Optional[IO | int] = None,
    stderr: Optional[IO | int] = None,
) -> subprocess.Popen:
    cmd = ['adb', *args]
    # New process group so that CTRL+C is only handled by this script.
    preexec_fn = os.setpgrp if os.name != 'nt' else None
    proc = subprocess.Popen(
        cmd, stdin=stdin, stdout=stdout, stderr=stderr, preexec_fn=preexec_fn
    )
    procs.append(proc)
    return proc


def adb_output(
    *args: str, stdin: Optional[IO | int] = devnull
) -> tuple[int, str]:
    proc = adb(*args, stdin=stdin, stdout=subprocess.PIPE)
    out = proc.communicate()[0].decode(errors='replace').strip()
    return proc.returncode, out


def kill_all_subprocs() -> None:
    for p in procs:
        if p.poll() is None:
            p.kill()


def broadcast(
    package: str, receiver: str, action: str
) -> tuple[Optional[int], Optional[str]]:
    """Sends an explicit broadcast and returns (result_code, data)."""
    _, out = adb_output(
        'shell', f'am broadcast -a {action} {package}/{receiver}'
    )
    # Broadcast completed: result=2, data="/storage/emulated/0/Android/media/..."
    match = re.search(
        r'Broadcast completed: result=(-?\d+)(?:, data="([^"]*)")?', out
    )
    if match is None:
        prt(f'Unexpected output for {action}:\n{out}', ANSI.RED)
        return None, None
    return int(match.group(1)), match.group(2)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=__doc__, formatter_class=argparse.RawTextHelpFormatter
    )
    parser.add_argument(
        '-p', '--package', required=True, help='Target application id'
    )
    parser.add_argument(
        '-o',
        '--out',
        default=os.getcwd(),
        help=(
            'Output .zip file or directory '
            '(default: ./<package>-recompose-<timestamp>.zip)'
        ),
    )
    parser.add_argument(
        '-c',
        '--config',
        default=None,
        help='Perfetto textproto config. Overrides -t and -b.',
    )
    parser.add_argument(
        '-t',
        '--time',
        type=int,
        default=0,
        help='Trace duration in seconds (default: until CTRL+C / SIGTERM)',
    )
    parser.add_argument(
        '-b',
        '--buffer',
        type=int,
        default=64,
        help='Ring buffer size in MB (default: 64)',
    )
    parser.add_argument(
        '-s', '--serial', default=None, help='ADB device serial'
    )
    parser.add_argument(
        '--no-recompose',
        action='store_true',
        help=(
            'Skip recomposition tracing (state reads/writes). Only'
            ' composition\ntracing (composable function sections) and the'
            ' system trace\nare captured.'
        ),
    )
    args = parser.parse_args()
    if args.config is not None and not os.path.exists(args.config):
        parser.error(f'Config file not found: {args.config}')
    return args


def resolve_output(out: str, fname: str) -> str:
    if out.endswith('/') or os.path.isdir(out):
        host_file = os.path.join(out, fname)
    else:
        host_file = out
    host_dir = os.path.dirname(os.path.abspath(host_file))
    os.makedirs(host_dir, exist_ok=True)
    return host_file


def check_device() -> None:
    code, out = adb_output('shell', 'getprop ro.build.version.sdk')
    if code != 0:
        prt('ADB connection failed', ANSI.RED)
        sys.exit(1)
    api_level = int(out.splitlines()[0].strip())
    if api_level < 29:
        prt(f'Android Q (API 29)+ is required, found API {api_level}', ANSI.RED)
        sys.exit(1)
    if api_level == 29:
        # traced is not enabled by default on Q.
        adb('shell', 'setprop persist.traced.enable 1').wait()


def start_in_process_tracing(package: str, recompose: bool) -> None:
    code, _ = broadcast(package, PROFILER_RECEIVER, PROFILER_ACTION_START)
    if code != PROFILER_RESULT_SUCCESS:
        prt(
            f'Failed to start in-process tracing (result={code}). Is the app '
            'running and does it depend on androidx.tracing:tracing-wire?',
            ANSI.RED,
        )
        sys.exit(1)
    prt('In-process tracing started')

    if not recompose:
        return
    code, _ = broadcast(package, RECOMPOSE_RECEIVER, RECOMPOSE_ACTION_START)
    if code == RECOMPOSE_RESULT_ALREADY_IN_PROGRESS:
        prt('Recomposition tracing was already in progress', ANSI.YELLOW)
    elif code != RECOMPOSE_RESULT_SUCCESS:
        broadcast(package, PROFILER_RECEIVER, PROFILER_ACTION_STOP)
        prt(
            f'Failed to start recomposition tracing (result={code}). Does the '
            'app depend on androidx.compose.runtime:runtime-tracing?',
            ANSI.RED,
        )
        sys.exit(1)
    else:
        prt('Recomposition tracing started')


def stop_in_process_tracing(package: str, recompose: bool) -> Optional[str]:
    """Stops in-process tracing, flushes it and returns the device directory."""
    if recompose:
        code, _ = broadcast(package, RECOMPOSE_RECEIVER, RECOMPOSE_ACTION_STOP)
        if code != RECOMPOSE_RESULT_SUCCESS:
            prt(
                f'Failed to stop recomposition tracing (result={code})',
                ANSI.RED,
            )
    code, _ = broadcast(package, PROFILER_RECEIVER, PROFILER_ACTION_STOP)
    if code != PROFILER_RESULT_SUCCESS:
        prt(f'Failed to stop in-process tracing (result={code})', ANSI.RED)

    code, path = broadcast(package, PROFILER_RECEIVER, PROFILER_ACTION_FLUSH)
    if code == PROFILER_RESULT_DELAYED_TRACE_DRIVER:
        prt(
            'The trace driver was not initialized eagerly, in-process traces '
            'may be incomplete',
            ANSI.YELLOW,
        )
    elif code != PROFILER_RESULT_FLUSH_COMPLETED:
        prt(f'Failed to flush in-process traces (result={code})', ANSI.RED)
        return None
    if not path:
        prt('Flush did not return a trace directory', ANSI.RED)
        return None
    return path


def start_perfetto(config: str, device_file: str) -> Optional[str]:
    """Starts perfetto in the background and returns its pid."""
    with tempfile.TemporaryFile() as f:
        f.write(config.encode())
        f.seek(0)
        cmd = [
            'perfetto',
            '--background',
            '--txt',
            '-c',
            '-',
            '-o',
            device_file,
        ]
        code, out = adb_output('shell', *cmd, stdin=f)
    match = re.search(r'^(\d+)$', out, re.M)
    if code != 0 or match is None:
        prt(f'Failed to start perfetto:\n{out}', ANSI.RED)
        return None
    return match.group(1)


def wait_for_perfetto(pid: str) -> None:
    """Waits for perfetto to exit. CTRL+C / SIGTERM stops the trace."""
    # Same approach as record_android_trace: avoid spawning processes on the
    # device while polling, as they pollute the trace.
    wait_cmd = (
        f'echo RUN; while [ -d /proc/{pid} ]; do read -t 1 scratch_var; '
        'done; echo TERM'
    )
    stop_count = 0
    while True:
        poll = None
        try:
            poll = adb(
                'shell',
                wait_cmd,
                stdin=subprocess.PIPE,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
            )
            for line in poll.stdout:
                if line.decode().strip() == 'TERM':
                    poll.wait()
                    return
            poll.wait()
            prt('ADB connection lost while waiting for perfetto', ANSI.RED)
            return
        except (KeyboardInterrupt, SignalException):
            if poll:
                poll.kill()
            sig = 'TERM' if stop_count == 0 else 'KILL'
            stop_count += 1
            prt(f'Stopping the trace (SIG{sig})', ANSI.BLACK + ANSI.BG_YELLOW)
            adb('shell', f'kill -{sig} {pid}').wait()
            if stop_count >= 2:
                return


def pull_in_process_traces(device_dir: str, host_dir: str) -> list[str]:
    code, out = adb_output('shell', f'ls -1 {device_dir}')
    if code != 0:
        prt(f'Failed to list {device_dir}:\n{out}', ANSI.RED)
        return []
    files = []
    for name in out.splitlines():
        name = name.strip()
        if not name:
            continue
        host_file = os.path.join(host_dir, name)
        if adb('pull', f'{device_dir}/{name}', host_file).wait() == 0:
            files.append(host_file)
    return files


def merge_traces(
    system_trace: Optional[str], in_process_traces: list[str], out_file: str
) -> None:
    with zipfile.ZipFile(out_file, 'w', zipfile.ZIP_DEFLATED) as zf:
        if system_trace is not None:
            zf.write(system_trace, 'system_trace.pftrace')
        for i, trace in enumerate(in_process_traces):
            zf.write(trace, f'in_process_{i}_{os.path.basename(trace)}')


def main() -> int:
    args = parse_args()
    if args.serial:
        os.environ['ANDROID_SERIAL'] = args.serial
    if shutil.which('adb') is None:
        prt('adb not found in PATH', ANSI.RED)
        return 1

    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)

    check_device()

    package: str = args.package
    recompose = not args.no_recompose
    tstamp = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S')
    base_name = f'{package}-recompose-{tstamp}'
    device_file = f'{DEVICE_TRACE_DIR}{base_name}.pftrace'
    out_file = resolve_output(args.out, f'{base_name}.zip')
    work_dir = tempfile.mkdtemp(prefix='recompose_trace_')

    system_trace: Optional[str] = None
    in_process_traces: list[str] = []
    try:
        start_in_process_tracing(package, recompose)
        pid: Optional[str] = None
        try:
            pid = start_perfetto(build_config(args), device_file)
            if pid is not None:
                prt(
                    'Trace started. Press CTRL+C or send SIGTERM to stop',
                    ANSI.BLACK + ANSI.BG_BLUE,
                )
                wait_for_perfetto(pid)
        finally:
            # Ignore further signals while tearing down, so that the in-process
            # tracing always gets stopped and flushed.
            signal.signal(signal.SIGINT, signal.SIG_IGN)
            signal.signal(signal.SIGTERM, signal.SIG_IGN)
            device_dir = stop_in_process_tracing(package, recompose)

        if pid is not None:
            system_trace = os.path.join(work_dir, 'system_trace.pftrace')
            prt('Pulling the system trace')
            if adb('pull', device_file, system_trace).wait() != 0:
                prt(f'Failed to pull {device_file}', ANSI.RED)
                system_trace = None
            adb('shell', f'rm -f {device_file}').wait()

        if device_dir is not None:
            prt(f'Pulling in-process traces from {device_dir}')
            in_process_dir = os.path.join(work_dir, 'in_process')
            os.makedirs(in_process_dir)
            in_process_traces = pull_in_process_traces(
                device_dir, in_process_dir
            )
            if not in_process_traces:
                prt('No in-process traces found', ANSI.YELLOW)

        if system_trace is None and not in_process_traces:
            prt('No traces collected', ANSI.RED)
            return 1

        merge_traces(system_trace, in_process_traces, out_file)
        prt(f'\nWrote {out_file}', ANSI.BOLD)
        if system_trace is not None:
            prt('  system_trace.pftrace')
        for trace in in_process_traces:
            prt(f'  {os.path.basename(trace)}')
    finally:
        kill_all_subprocs()
        shutil.rmtree(work_dir, ignore_errors=True)

    return 0


if __name__ == '__main__':
    sys.exit(main())
