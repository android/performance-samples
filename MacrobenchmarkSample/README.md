## Macrobenchmark Sample

This sample project shows how to use the Jetpack Macrobenchmark library.

See the [Macrobenchmark guide](https://developer.android.com/studio/profile/macrobenchmark-intro) for more information on the library.

### Samples

The sample project includes the following:

* [TrivialStartupBenchmark](macrobenchmark/src/main/java/com/example/macrobenchmark/TrivialStartupBenchmark.kt) measures the application startup time for a trivial `Activity` with a single `TextView`.

* [SmallListStartupBenchmark](macrobenchmark/src/main/java/com/example/macrobenchmark/SmallListStartupBenchmark.kt) measures the application startup time for an `Activity` with a `RecyclerView`.

* [FrameTimingBenchmark](macrobenchmark/src/main/java/com/example/macrobenchmark/FrameTimingBenchmark.kt) scrolls a simple `RecyclerView`, measuring frame timing / jank.

### Running

Open the `MacrobenchmarkSample` project in Android Studio Arctic Fox, Beta 1 or later, and run benchmarks as you usually would run tests: Ctrl-Shift-F10 (Mac: Ctrl-Shift-R)

License
-------

Copyright 2021 The Android Open Source Project, Inc.

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
