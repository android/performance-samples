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
            startApp()

            onView {
                val resourceName =
                    if (useRecyclerViewPool) "nestedRecyclerWithPoolsActivity" else "nestedRecyclerActivity"
                viewIdResourceName == resourceName
            }.click()

            // wait until the activity is shown
            onView { className == "$packageName.NestedRecyclerActivity" }
        }
    }

    private fun MacrobenchmarkScope.measureScrollingNestedRecycler() =
        uiAutomator {
            val recycler = onView { viewIdResourceName == "recycler" }
            // Set gesture margin to avoid triggering gesture navigation with input events from automation.
            recycler.setGestureMargin(device.displayWidth / 5)

            repeat(3) { index ->
                // scroll the second recycler, because the first one may be shown just a pixel
                val nestedRecyclerToScroll = onViews { viewIdResourceName == "row_recycler" }[1]

                with(nestedRecyclerToScroll) {
                    // Set gesture margin to avoid triggering gesture navigation with input events from automation.
                    setGestureMargin(device.displayWidth / 3)

                    // swipe horizontally
                    fling(Direction.RIGHT)
                    // scroll down twice and once up
                    swipe(if (index < 2) Direction.UP else Direction.DOWN, 0.5f)
                    // wait until the swipe is done
                }
            }
        }
}
