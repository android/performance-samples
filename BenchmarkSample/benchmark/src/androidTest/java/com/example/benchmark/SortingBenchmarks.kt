package com.example.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.ui.SortingAlgorithms
import com.example.benchmark.ui.isSorted
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random


@RunWith(AndroidJUnit4::class)
class SortingBenchmarks {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    // [START benchmark_with_timing_disabled]
    // using random with the same seed, so that it generates the same data every run
    private val random = Random(0)

    // create the array once and just copy it in benchmarks
    private val unsorted = IntArray(10_000) { random.nextInt() }

    @Test
    fun benchmark_quickSort() {
        // [START_EXCLUDE]
        // creating the variable outside of the measureRepeated to be able to assert after done
        var listToSort = intArrayOf()
        // [END_EXCLUDE]
        benchmarkRule.measureRepeated {
            // copy the array with timing disabled to measure only the algorithm itself
            listToSort = runWithTimingDisabled { unsorted.copyOf() }

            // sort the array in place and measure how long it takes
            SortingAlgorithms.quickSort(listToSort)
        }

        // assert only once not to add overhead to the benchmarks
        assertTrue(listToSort.isSorted)
    }
    // [END benchmark_with_timing_disabled]

    @Test
    fun benchmark_bubbleSort() {
        // creating the variable outside of the measureRepeated to be able to assert after done
        var listToSort = intArrayOf()

        benchmarkRule.measureRepeated {
            // copy the array with timing disabled to measure only the algorithm itself
            listToSort = runWithTimingDisabled { unsorted.copyOf() }

            // sort the array in place and measure how long it takes
            SortingAlgorithms.bubbleSort(listToSort)
        }

        // assert only once not to add overhead to the benchmarks
        assertTrue(listToSort.isSorted)
    }
}
