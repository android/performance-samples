# Compose Recomposition Tracing

[`record_recomposition_trace.py`](record_recomposition_trace.py) records a
[Perfetto](https://perfetto.dev) system trace together with Compose composition
and recomposition tracing from your app, and merges everything into a single
file that you can open in [ui.perfetto.dev](https://ui.perfetto.dev).

Recomposition tracing shows which state reads and writes caused a composable to
recompose. Composition tracing adds a trace section for each composable
function. Both have a higher overhead than regular tracing, so use them for
debugging, not for measuring performance.

## Setup

Add the `runtime-tracing` dependency to your app module:

```kotlin
dependencies {
    // runtime-tracing 1.13.0 or newer is required
    implementation("androidx.compose.runtime:runtime-tracing:1.13.0")
}
```

You also need:

* A device running Android 10 (API 29) or newer, connected over `adb`.
* Python 3.10 or newer.
* `adb` on your `PATH`.

## Usage

Start the app, then run:

```shell
./record_recomposition_trace.py -p com.example.app
```

Interact with the app and press `Ctrl+C` to stop recording. To record for a
fixed time instead, pass the duration in seconds:

```shell
./record_recomposition_trace.py -p com.example.app -t 10
```

The script writes `<package>-recompose-<timestamp>.zip` to the current
directory. Open the file in [ui.perfetto.dev](https://ui.perfetto.dev) or load it
with `trace_processor`.

> [!NOTE]
> If the trace has no Compose events, check that animations are enabled on the
> device (Developer options > Animator duration scale) and that the app
> recomposes while the script is recording.

### Options

| Option | Default | Description |
|---|---|---|
| `-p, --package` | (required) | Application id of the app to trace. |
| `-o, --out` | current directory | Output `.zip` file or directory. |
| `-t, --time` | `0` | Trace duration in seconds. `0` records until `Ctrl+C` or `SIGTERM`. |
| `-b, --buffer` | `64` | Size of the system trace ring buffer in MB. |
| `-c, --config` | built-in config | Custom Perfetto text config. Overrides `-t` and `-b`. |
| `-s, --serial` | `$ANDROID_SERIAL` | Serial of the device to use. |
| `--no-recompose` | off | Skip recomposition tracing (state reads and writes). Only composition tracing and the system trace are captured. |
