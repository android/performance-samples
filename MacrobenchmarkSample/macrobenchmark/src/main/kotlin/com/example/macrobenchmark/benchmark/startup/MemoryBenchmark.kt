package com.example.macrobenchmark.benchmark.startup


import androidx.test.filters.SdkSuppress
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.macrobenchmark.benchmark.util.DEFAULT_ITERATIONS
import com.example.macrobenchmark.benchmark.util.TARGET_PACKAGE
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MemoryStartupBenchmark {
    @get:Rule
    val benchmarkRule = Macrobench()

    @SdkSuppress(minSdkVersion = 36)
    @androidx.benchmark.ExperimentalBenchmarkConfigApi
    @androidx.benchmark.macro.ExperimentalMetricApi
    @Test
    fun startupMemory() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(
            StartupTimingMetric(),
            androidx.benchmark.macro.MemoryUsageMetric(
                androidx.benchmark.macro.MemoryUsageMetric.Mode.Last,
                listOf(
                    androidx.benchmark.macro.MemoryUsageMetric.SubMetric.HeapSize,
                    androidx.benchmark.macro.MemoryUsageMetric.SubMetric.RssAnon,
                    androidx.benchmark.macro.MemoryUsageMetric.SubMetric.Swap
                )
            )
        ),
        compilationMode = CompilationMode.None(),
        iterations = DEFAULT_ITERATIONS,
        startupMode = StartupMode.COLD,
        experimentalConfig = androidx.benchmark.ExperimentalConfig(
            memoryProfilingConfig = androidx.benchmark.MemoryProfilingConfig(
                isSampleArtHeapEnabled = true,
                isSampleNativeHeapEnabled = true
            )
        )
    ) {
        startActivityAndWait()
    }
}