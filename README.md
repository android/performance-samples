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

