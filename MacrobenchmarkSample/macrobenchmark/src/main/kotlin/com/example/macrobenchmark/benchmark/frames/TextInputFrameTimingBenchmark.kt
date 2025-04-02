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
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.uiAutomator
import com.example.macrobenchmark.benchmark.util.DEFAULT_ITERATIONS
import com.example.macrobenchmark.benchmark.util.TARGET_PACKAGE
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class TextInputFrameTimingBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun inputText() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            // Try switching to different compilation modes to see the effect
            // it has on frame timing metrics.
            compilationMode = CompilationMode.None(),
            startupMode = StartupMode.WARM, // restarts activity each iteration
            iterations = DEFAULT_ITERATIONS,
            setupBlock = {
                // Before starting to measure, navigate to the UI to be measured
                uiAutomator {
                    startIntent(Intent("$packageName.COMPOSE_ACTIVITY"))
                }
            }
        ) {
            uiAutomator {
                with(onView { viewIdResourceName == "input" }) {
                    repeat(3) {
                        text = ""
                        text = "Benchmark input $it"
                    }
                }
            }
        }
    }
}
