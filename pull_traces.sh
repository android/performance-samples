#!/bin/bash

# The following command pulls all files ending in .perfetto-trace from the directory
# hierarchy starting at the root /storage/emulated/0/Android.
adb shell find /storage/emulated/0/Android/ -name "*.perfetto-trace" \
    | tr -d '\r' | xargs -n1 adb pull