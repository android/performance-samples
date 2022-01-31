#!/bin/sh

current_dir=`basename $(pwd)`
if [ $current_dir != "ConstraintLayoutPerformance" ];then
    echo 'You need to run the script at the "ConstraintLayoutPerformance" directory'
    exit 1
fi

if [ -z $ANDROID_HOME ];then
    echo "You need to set the ANDROID_HOME environment variable"
    exit 1;
fi

if [ $# -lt 1 ];then
    echo "Usage: $0 <device_id> "
    echo "Ex.) $0 00000xxxx11"
    exit 1
fi

ADB=${ANDROID_HOME}/platform-tools/adb
DEVICE_ID=$1

function run_test_and_systrace {
  test_method=$1
  ${ADB} -s ${DEVICE_ID} shell am instrument -w -r -e debug false -e class com.example.android.perf.test.PerformanceTest\#${test_method} com.example.android.perf.test/android.support.test.runner.AndroidJUnitRunner &
  adb_pid=$!
  python ${ANDROID_HOME}/platform-tools/systrace/systrace.py --serial=${DEVICE_ID} --time=20 -o ./trace_${test_method}.html gfx view res &
  systrace_pid=$!

  echo "adb_pid = ${adb_pid}"
  echo "systrace_pid = ${systrace_pid}"

  wait ${adb_pid}
  echo "Instrumentation finished for $test_method"
  wait ${systrace_pid}
  echo "Systrace finished for $test_method"
}

./gradlew installDebug
run_test_and_systrace testRunCalculationConstraintLayout
run_test_and_systrace testRunCalculationTraditionalLayouts

