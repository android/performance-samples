Benchmark NDK Sample (Prototype)
===================================

### Setup
To use the this sample, you will need to run the following command to
download the google/benchmark dependency:

```
git pull --recurse-submodules
```

You will also need the NDK, and CMake dependencies installed. You can do this
from Android Studio Settings, go to `Appearance & Behavior` > `System Settings`
> `Android SDK`, `SDK Tools` tab.

### Intro

This sample project shows how to use the Jetpack Benchmark library together
with the NDK.

It layers most of the features of the Jetpack Benchmark library on top of
https://github.com/google/benchmark, including:

 * Clock locking (script, Sustained Performance mode, or throttle detection)
 * Activity launch to run Benchmark in foreground
 * Joined benchmark data reporting through JSON/XML
 * Running and reporting through Android Studio

### Running

Open the project in Android Studio 3.4 or later, and run benchmarks as you
usually would run tests: Ctrl-Shift-F10 (Mac: Ctrl-Shift-R)

### Locking Clocks

If you have a rooted device you can benchmark on, use `./gradlew lockClocks` to
lock the CPU clocks of the device to stable values. To unlock clocks, use
`./gradlew unlockClocks`, or reboot your device. This feature is provided by the
`androidx.benchmark` gradle plugin.

License
-------

Copyright 2019 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
