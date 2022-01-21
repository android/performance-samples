#!/bin/bash

# The following command pulls all files ending in -benchmarkData.json from the directory
# hierarchy starting at the root /storage/emulated/0/Android.
adb shell find /storage/emulated/0/Android/ -name "*-benchmarkData.json" \
    | tr -d '\r' | xargs -n1 adb pull