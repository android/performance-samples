/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.macrobenchmark.benchmark.frames

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.onElement
import androidx.test.uiautomator.textAsString
import androidx.test.uiautomator.uiAutomator
import com.example.macrobenchmark.benchmark.util.DEFAULT_ITERATIONS
import com.example.macrobenchmark.benchmark.util.TARGET_PACKAGE
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalMetricApi
@LargeTest
@RunWith(AndroidJUnit4::class)
class NestedRecyclerFrameTimingBenchmarks {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollNestedRecyclerWithoutRecyclerPool() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            startupMode = StartupMode.WARM,
            iterations = DEFAULT_ITERATIONS,
            setupBlock = { navigateToNestedRvScreen(false) }
        ) { measureScrollingNestedRecycler() }
    }

    @Test
    fun scrollNestedRecyclerWithRecyclerPool() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            startupMode = StartupMode.WARM,
            iterations = DEFAULT_ITERATIONS,
            setupBlock = { navigateToNestedRvScreen(true) }
        ) { measureScrollingNestedRecycler() }
    }

    private fun MacrobenchmarkScope.navigateToNestedRvScreen(useRecyclerViewPool: Boolean) {
        uiAutomator {
            startApp(TARGET_PACKAGE)
            onElement { textAsString() == if (useRecyclerViewPool) "Nested RecyclerView with Pools" else "Nested RecyclerView" }.click()
            waitForStableInActiveWindow()
        }
    }

    private fun MacrobenchmarkScope.measureScrollingNestedRecycler() {
        uiAutomator {
            onElement { className == "androidx.recyclerview.widget.RecyclerView" }.run {
                repeat(3) { index ->
                    onElement { className == "androidx.recyclerview.widget.RecyclerView" }.fling(
                        Direction.RIGHT
                    )
                    swipe(if (index < 2) Direction.UP else Direction.DOWN, 0.5f)
                }
            }
        }
    }

}

