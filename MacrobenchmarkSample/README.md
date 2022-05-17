# Macrobenchmark Sample

This sample project shows how to use the Jetpack Macrobenchmark library.

See the [Macrobenchmark guide](https://developer.android.com/studio/profile/macrobenchmark-intro) for more information on the library.

### Samples

The sample project includes the following:

* [SampleStartupBenchmark](macrobenchmark/src/main/java/com/example/macrobenchmark/startup/SampleStartupBenchmark.kt) measures the application startup time for a trivial `Activity` with a single `TextView`.

* [SmallListStartupBenchmark](macrobenchmark/src/main/java/com/example/macrobenchmark/startup/SmallListStartupBenchmark.kt) measures the application startup time for an `Activity` with a `RecyclerView` and an `Activity` with a Compose `LazyColumn`.

* [FrameTimingBenchmark](macrobenchmark/src/main/java/com/example/macrobenchmark/frames/FrameTimingBenchmark.kt) scrolls a simple `RecyclerView` and Compose `LazyColumn`, measuring frame timing / jank.

### Running

Open the `MacrobenchmarkSample` project in Android Studio Bumblebee or later, and run benchmarks as you usually would run tests: Ctrl-Shift-F10 (Mac: Ctrl-Shift-R)

Alternatively, run the benchmarks from terminal with: 
```
./gradlew macrobenchmark:cC
```

### Reporting Issues

You can report an [Issue with the sample](https://github.com/googlesamples/android-performance/issues) using this repository. If you find an issue with the Macrobenchmark library, report it using the [Issue Tracker](https://issuetracker.google.com/issues/new?component=975669&template=1519452).

License
-------

Copyright 2022 The Android Open Source Project, Inc.

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
