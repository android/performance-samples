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

package com.example.macrobenchmark.frames

import androidx.benchmark.macro.*
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import com.example.macrobenchmark.TARGET_PACKAGE
import com.example.macrobenchmark.waitUntilActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalMetricApi
@LargeTest
@RunWith(AndroidJUnit4::class)
class NestedRecyclerFrameTimingBenchmarks {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private val metrics = listOf(
        FrameTimingMetric(),
        TraceSectionMetric("ParentAdapter.onCreateViewHolder"),
        TraceSectionMetric("ParentAdapter.onBindViewHolder"),
        TraceSectionMetric("ChildAdapter.onCreateViewHolder"),
        TraceSectionMetric("ChildAdapter.onBindViewHolder"),
    )

    @Test
    fun scrollNestedRecyclerWithoutRecyclerPool() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = metrics,
            startupMode = StartupMode.WARM, // restarts the activity each iteration
            iterations = 10,
            setupBlock = { navigateToNestedRvScreen(false) }
        ) { measureScrollingNestedRecycler() }
    }

    @Test
    fun scrollNestedRecyclerWithRecyclerPool() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = metrics,
            startupMode = StartupMode.WARM, // restarts the activity each iteration
            iterations = 10,
            setupBlock = { navigateToNestedRvScreen(true) }
        ) { measureScrollingNestedRecycler() }
    }

    private fun MacrobenchmarkScope.navigateToNestedRvScreen(useRecyclerViewPool: Boolean) {
        startActivityAndWait()

        // navigate to the activity
        val buttonId =
            if (useRecyclerViewPool) "nestedRecyclerWithPoolsActivity" else "nestedRecyclerActivity"

        device
            .findObject(By.res(packageName, buttonId))
            .click()

        // wait until the activity is shown
        device.waitUntilActivity("$packageName.NestedRecyclerActivity")
    }

    private fun MacrobenchmarkScope.measureScrollingNestedRecycler() {
        val recycler = device.findObject(By.res(packageName, "recycler"))
        // Set gesture margin to avoid triggering gesture navigation with input events from automation.
        recycler.setGestureMargin(device.displayWidth / 5)

        repeat(3) { index ->
            val visibleNestedRecyclers =
                recycler.findObjects(By.res(packageName, "row_recycler"))

            // scroll the second recycler, because the first one may be shown just a pixel
            val nestedRecyclerToScroll = visibleNestedRecyclers[1]

            // Set gesture margin to avoid triggering gesture navigation with input events from automation.
            nestedRecyclerToScroll.setGestureMargin(device.displayWidth / 5)

            // swipe horizontally
            nestedRecyclerToScroll.fling(Direction.RIGHT)
            // scroll down twice and once up
            recycler.swipe(if (index < 2) Direction.UP else Direction.DOWN, 0.5f)
            // wait until the swipe is done
            device.waitForIdle()
        }
    }

}

