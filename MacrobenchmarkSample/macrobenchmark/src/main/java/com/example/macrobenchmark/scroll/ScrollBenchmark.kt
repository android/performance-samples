/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.macrobenchmark.scroll

import android.content.Intent
import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SdkSuppress
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import com.example.benchmark.macro.base.util.DEFAULT_ITERATIONS
import com.example.benchmark.macro.base.util.TARGET_PACKAGE
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMetricApi::class)
class ScrollBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    @SdkSuppress(minSdkVersion = 24)
    fun partialBaselineProfileRequired() =
        scroll(
            CompilationMode.Partial(
                baselineProfileMode = BaselineProfileMode.Require,
                warmupIterations = 3
            )
        )

    @Test
    fun full() = scroll(CompilationMode.Full())

    private fun scroll(compilationMode: CompilationMode) {
        var firstStart = true
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(
                TraceSectionMetric("ClickTrace"),
                StartupTimingMetric(),
                FrameTimingMetric()
            ),
            compilationMode = CompilationMode.Full(),
            startupMode = null,
            iterations = DEFAULT_ITERATIONS,
            setupBlock = {
                if (firstStart) {
                    val intent = Intent("$packageName.SCROLL_VIEW_ACTIVITY")
                    startActivityAndWait(intent)
                    firstStart = false
                }
            }
        ) {
            device.wait(Until.hasObject(By.scrollable(true)), 5_000)

            val scrollableObject = device.findObject(By.scrollable(true))
            if (scrollableObject == null) {
                TestCase.fail("No scrollable view found in hierarchy")
            }
            scrollableObject.setGestureMargin(device.displayWidth / 10)
            scrollableObject?.apply {
                repeat(2) {
                    fling(Direction.DOWN)
                }
                repeat(2) {
                    fling(Direction.UP)
                }
            }
        }
    }
}
