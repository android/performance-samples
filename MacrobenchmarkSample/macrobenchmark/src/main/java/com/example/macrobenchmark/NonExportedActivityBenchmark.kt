/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.macrobenchmark

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@LargeTest
@SdkSuppress(minSdkVersion = 29)
@RunWith(AndroidJUnit4::class)
class NonExportedActivityBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scroll() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val device = UiDevice.getInstance(instrumentation)
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            // Try switching to different compilation modes to see the effect
            // it has on frame timing metrics.
            compilationMode = CompilationMode.None,
            iterations = 3,
            setupBlock = {
                // Before starting to measure, navigate to the UI to be measured
                val intent = Intent()
                intent.action = ACTION
                // Ensures that a new activity is created every single time
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivityAndWait(intent)
                // click a button to launch the target activity.
                // While we use resourceId here to find the button, you could also use
                // accessibility info or button text content.
                val launchRecyclerActivity = device.findObject(
                    By.res(PACKAGE_NAME, LAUNCH_RESOURCE_ID)
                )
                launchRecyclerActivity.click()
                device.waitUntilActivity(RECYCLER_ACTIVITY_CLASS)
            }
        ) {
            val recycler = device.findObject(
                By.res(
                    PACKAGE_NAME,
                    RECYCLER_RESOURCE_ID
                )
            )
            // Set gesture margin to avoid triggering gesture navigation
            // with input events from automation.
            recycler.setGestureMargin(device.displayWidth / 5)
            for (i in 1..10) {
                recycler.scroll(Direction.DOWN, 2f)
                device.waitForIdle()
            }
        }
    }

    companion object {
        private const val PACKAGE_NAME = "com.example.macrobenchmark.target"
        private const val RECYCLER_ACTIVITY_CLASS = "$PACKAGE_NAME.NonExportedRecyclerActivity"
        private const val ACTION = "$PACKAGE_NAME.ACTIVITY_LAUNCHER_ACTIVITY"
        private const val LAUNCH_RESOURCE_ID = "launchRecyclerActivity"
        private const val RECYCLER_RESOURCE_ID = "recycler"

        /**
         * Waits until an [android.app.Activity] with the given `className` is visible.
         */
        fun UiDevice.waitUntilActivity(className: String) {
            wait(
                Until.hasObject(
                    By.clazz(className)
                ),
                TimeUnit.SECONDS.toMillis(10)
            )
        }
    }
}
