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

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import com.example.macrobenchmark.TARGET_PACKAGE
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val ITERATIONS = 10

@LargeTest
@RunWith(AndroidJUnit4::class)
class FrameTimingBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    // [START macrobenchmark_control_your_app]
    @Test
    fun scrollList() {
        benchmarkRule.measureRepeated(
            // [START_EXCLUDE]
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            // Try switching to different compilation modes to see the effect
            // it has on frame timing metrics.
            compilationMode = CompilationMode.None(),
            startupMode = StartupMode.WARM, // restarts activity each iteration
            iterations = ITERATIONS,
            // [END_EXCLUDE]
            setupBlock = {
                // Before starting to measure, navigate to the UI to be measured
                val intent = Intent("$packageName.RECYCLER_VIEW_ACTIVITY")
                startActivityAndWait(intent)
            }
        ) {
            val recycler = device.findObject(By.res(packageName, "recycler"))
            // Set gesture margin to avoid triggering gesture navigation
            // with input events from automation.
            recycler.setGestureMargin(device.displayWidth / 5)

            // Scroll down several times
            repeat(3) { recycler.fling(Direction.DOWN) }
        }
    }
    // [END macrobenchmark_control_your_app]

    @Test
    fun scrollComposeList() {
        benchmarkRule.measureRepeated(
            // [START_EXCLUDE]
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            // Try switching to different compilation modes to see the effect
            // it has on frame timing metrics.
            compilationMode = CompilationMode.None(),
            startupMode = StartupMode.WARM, // restarts activity each iteration
            iterations = ITERATIONS,
            // [END_EXCLUDE]
            setupBlock = {
                // Before starting to measure, navigate to the UI to be measured
                val intent = Intent("$packageName.COMPOSE_ACTIVITY")
                startActivityAndWait(intent)
            }
        ) {
            /**
             * Compose does not have view IDs so we cannot directly access composables from UiAutomator.
             * To access a composable we need to set:
             * 1) Modifier.semantics { testTagsAsResourceId = true } once, high in the compose hierarchy
             * 2) Add Modifier.testTag("someIdentifier") to all of the composables you want to access
             *
             * Once done that, we can access the composable using By.res("someIdentifier")
             */
            val column = device.findObject(By.res("myLazyColumn"))

            // Set gesture margin to avoid triggering gesture navigation
            // with input events from automation.
            column.setGestureMargin(device.displayWidth / 5)

            // Scroll down several times
            repeat(3) { column.fling(Direction.DOWN) }
        }
    }
}
