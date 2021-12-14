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
import kotlin.random.Random.Default.nextInt


@RunWith(AndroidJUnit4::class)
class SortingBenchmarks {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    // [START benchmark_with_timing_disabled]
    @Test
    fun benchmark_quickSort() {
        benchmarkRule.measureRepeated {
            // create a random array to sort without measuring because we care only about the algorithm itself
            val listToSort = runWithTimingDisabled {
                IntArray(10_000) { nextInt() }
            }

            // sort the array and measure how long it takes
            SortingAlgorithms.quickSort(listToSort)

            // verify the array is sorted
            runWithTimingDisabled { assertTrue(listToSort.isSorted) }
        }
    }
    // [END benchmark_with_timing_disabled]

    @Test
    fun benchmark_bubbleSort() {
        benchmarkRule.measureRepeated {
            // create a random array to sort without measuring because we care only about the algorithm itself
            val listToSort = runWithTimingDisabled {
                IntArray(10_000) { nextInt() }
            }

            // sort the array and measure how long it takes
            SortingAlgorithms.bubbleSort(listToSort)

            // verify the array is sorted
            runWithTimingDisabled { assertTrue(listToSort.isSorted) }
        }
    }
}