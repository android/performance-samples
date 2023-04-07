Android Performance
===================================

A collection of samples using the performance libraries.

You can learn more about Android Performance at https://d.android.com/performance.

## [Macrobenchmark sample](MacrobenchmarkSample)

This sample shows how to use the Macrobenchmark library for testing application startup and runtime
performance cases, such as scrolling a `RecyclerView` or `LazyColumn` to measure jank.

**Baseline Profiles** can be generated with the Macrobenchmark library. This sample contains
[code to generate a baseline profile](MacrobenchmarkSample/macrobenchmark/src/main/java/com/example/macrobenchmark/baselineprofile/BaselineProfileGenerator.kt).

The sample also includes a [GitHub workflow](.github/workflows/firebase_test_lab.yml) to run
Macrobenchmarks on Firebase Test Lab. For more information refer to the [README.md](MacrobenchmarkSample/ftl/README.md).

The Macrobenchmark sample also demonstrates using [Jetpack Compose with Macrobenchmark](MacrobenchmarkSample/macrobenchmark/src/main/java/com/example/macrobenchmark/frames/FrameTimingBenchmark.kt#L72).

## [Microbenchmark sample](MicrobenchmarkSample)

This sample shows how to use the Benchmark library to benchmark code and UI from library modules.

### Important Notes

* Make sure your device's screen is on before running benchmarks
* Don't forget to checkout submodules: `git submodule update --init --recursive`

## [JankStats sample](JankStatsSample)

This sample shows how to set up and use the JankStats library to detect janky frames. 

## Reporting Issues

You can report an [Issue with a sample](https://github.com/android/performance-samples/issues) using
this repository. If you find an issue with a specific library, report it using the corresponding
issue tracker link available in the sample README file.

## Additional Resources

[PagingWithNetworkSample](https://github.com/googlesamples/android-architecture-components/tree/master/PagingWithNetworkSample)
- Example of benchmarking an androidx.paging backed RecyclerView

[WorkManagerSample](https://github.com/googlesamples/android-architecture-components/tree/master/WorkManagerSample)
- Example of benchmarking asynchronously scheduled background work

### License 

```
Copyright 2022 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.
See the NOTICE file distributed with this work for additional information regarding copyright
ownership.  The ASF licenses this file to you under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.  You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied.  See the License for the specific language governing permissions and limitations under the
License.
```
