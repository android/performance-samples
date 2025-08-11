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

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
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
            iterations = DEFAULT_ITERATIONS,
            // [END_EXCLUDE]
            setupBlock = {
                uiAutomator {
                    // Before starting to measure, navigate to the UI to be measured
                    startIntent(Intent("$packageName.RECYCLER_VIEW_ACTIVITY"))
                }
            }
        ) {
            uiAutomator {
                val recycler = onElement { className == "androidx.recyclerview.widget.RecyclerView" }
                // Scroll down several times
                repeat(3) { recycler.fling(Direction.DOWN) }
            }

        }
    }
    // [END macrobenchmark_control_your_app]

    @OptIn(ExperimentalMetricApi::class)
    @Test
    fun scrollComposeList() {
        benchmarkRule.measureRepeated(
            // [START_EXCLUDE]
            packageName = TARGET_PACKAGE,
            metrics = listOf(
                FrameTimingMetric(),
                // Measure custom trace sections by name EntryRow (which is added to the EntryRow composable).
                // Mode.Sum measure combined duration and also how many times it occurred in the trace.
                // This way, you can estimate whether a composable recomposes more than it should.
                TraceSectionMetric("EntryRowCustomTrace", TraceSectionMetric.Mode.Sum),
                // This trace section takes into account the SQL wildcard character %,
                // which can find trace sections without knowing the full name.
                // This way, you can measure composables produced by the composition tracing
                // and measure how long they took and how many times they recomposed.
                // WARNING: This metric only shows results when running with composition tracing, otherwise it won't be visible in the outputs.
                TraceSectionMetric("%EntryRow (%", TraceSectionMetric.Mode.Sum),
            ),
            // Try switching to different compilation modes to see the effect
            // it has on frame timing metrics.
            compilationMode = CompilationMode.None(),
            startupMode = StartupMode.WARM, // restarts activity each iteration
            iterations = DEFAULT_ITERATIONS,
            // [END_EXCLUDE]
            setupBlock = {
                uiAutomator {
                    // Before starting to measure, navigate to the UI to be measured
                    startIntent(Intent("$packageName.COMPOSE_ACTIVITY"))
                }
            }
        ) {
            uiAutomator {
                onElement { isScrollable }.fling(Direction.DOWN)
            }
        }
    }
}
