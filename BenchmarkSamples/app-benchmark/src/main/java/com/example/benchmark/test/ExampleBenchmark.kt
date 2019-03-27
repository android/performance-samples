package com.example.benchmark.test

import androidx.benchmark.BenchmarkRule
import androidx.test.runner.AndroidJUnit4
import com.example.benchmark.app.SampleClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Shows how to write a benchmark that accesses classes defined in your :app module
 */
@RunWith(AndroidJUnit4::class)
class ExampleBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun sleep() {
        benchmarkRule.measure {
            SampleClass.sleep()
        }
    }
}
