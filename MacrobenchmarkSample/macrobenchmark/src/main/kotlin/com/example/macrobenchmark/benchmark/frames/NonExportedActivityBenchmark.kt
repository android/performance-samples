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

import androidx.benchmark.macro.CompilationMode
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


@LargeTest
@RunWith(AndroidJUnit4::class)
class NonExportedActivityBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Showcase how to access non exported activity by navigating into it from default one.
     */
    // [START macrobenchmark_navigate_within_app]
    @Test
    fun nonExportedActivityScrollList() {
        benchmarkRule.measureRepeated(
            // [START_EXCLUDE]
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            // Try switching to different compilation modes to see the effect
            // it has on frame timing metrics.
            compilationMode = CompilationMode.None(),
            startupMode = StartupMode.WARM, // Ensures that a new activity is created every single time
            iterations = DEFAULT_ITERATIONS,
            // [END_EXCLUDE]
            setupBlock = setupBenchmark()
        ) {
            uiAutomator {
                // [START_EXCLUDE]
                with(onView { viewIdResourceName == "recycler" }) {

                    // Set gesture margin to avoid triggering gesture navigation
                    // with input events from automation.
                    setGestureMargin(uiDevice.displayWidth / 5)

                    // Fling the recycler several times
                    repeat(3) { fling(Direction.DOWN) }
                }
                // [END_EXCLUDE]
            }
        }
    }

    private fun setupBenchmark(): MacrobenchmarkScope.() -> Unit = {
        // Before starting to measure, navigate to the UI to be measured
        uiAutomator {
            startApp()

            // click a button to launch the target activity.
            // While we use button text  here to find the button, you could also use
            // accessibility info or resourceId.
            onView { text == "RecyclerView" }.click()

            // wait until the activity is shown
            onView { className == "$packageName.NonExportedRecyclerActivity" }
        }
    }
    // [END macrobenchmark_navigate_within_app]

}
