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

package com.example.macrobenchmark.clicks;

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.example.macrobenchmark.TARGET_PACKAGE
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val ITERATIONS = 10

/**
 * These tests measure click handling time, i.e. the time from when a touch up event is received
 * to when the click listener is fired.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMetricApi::class)
class ClickLatencyBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun simpleViewClick() {
        var firstStart = true
        benchmarkRule.measureRepeated(
                packageName = TARGET_PACKAGE,
                metrics = listOf(TraceSectionMetric("ClickTrace")),
                compilationMode = CompilationMode.Full(),
                startupMode = null,
                iterations = ITERATIONS,
                setupBlock = {
                    if (firstStart) {
                        startActivityAndWait()
                        firstStart = false
                    }
                }
        ) {
            clickOnId("launchRecyclerActivity")
            waitForTextShown("RecyclerView Sample")
            device.pressBack()
            waitForTextGone("RecyclerView Sample")
        }
    }

    @Test
    fun recyclerViewClick() {
        var firstStart = true
        benchmarkRule.measureRepeated(
                packageName = TARGET_PACKAGE,
                metrics = listOf(TraceSectionMetric("ClickTrace")),
                compilationMode = CompilationMode.Full(),
                startupMode = null,
                iterations = ITERATIONS,
                setupBlock = {
                    if (firstStart) {
                        val intent = Intent("$packageName.RECYCLER_VIEW_ACTIVITY")
                        startActivityAndWait(intent)
                        firstStart = false
                    }
                }
        ) {
            clickOnFirstItem()
        }
    }

    @Test
    fun composeLazyColumnClick() {
        var firstStart = true
        benchmarkRule.measureRepeated(
                packageName = TARGET_PACKAGE,
                metrics = listOf(TraceSectionMetric("ClickTrace")),
                compilationMode = CompilationMode.Full(),
                startupMode = null,
                iterations = ITERATIONS,
                setupBlock = {
                    if (firstStart) {
                        val intent = Intent("$packageName.COMPOSE_ACTIVITY")
                        startActivityAndWait(intent)
                        firstStart = false
                    }
                }
        ) {
            clickOnFirstItem()
        }
    }

    @Test
    fun listViewClick() {
        var firstStart = true
        benchmarkRule.measureRepeated(
                packageName = TARGET_PACKAGE,
                metrics = listOf(TraceSectionMetric("ClickTrace")),
                compilationMode = CompilationMode.Full(),
                startupMode = null,
                iterations = ITERATIONS,
                setupBlock = {
                    if (firstStart) {
                        val intent = Intent("$packageName.LIST_VIEW_ACTIVITY")
                        startActivityAndWait(intent)
                        firstStart = false
                    }
                }
        ) {
            clickOnFirstItem()
        }
    }

    @Test
    fun scrollViewClick() {
        var firstStart = true
        benchmarkRule.measureRepeated(
                packageName = TARGET_PACKAGE,
                metrics = listOf(TraceSectionMetric("ClickTrace")),
                compilationMode = CompilationMode.Full(),
                startupMode = null,
                iterations = ITERATIONS,
                setupBlock = {
                    if (firstStart) {
                        val intent = Intent("$packageName.SCROLL_VIEW_ACTIVITY")
                        startActivityAndWait(intent)
                        firstStart = false
                    }
                }
        ) {
            clickOnFirstItem()
        }
    }

    private fun MacrobenchmarkScope.clickOnFirstItem() {
        clickOnText("Item 0")
        waitForTextShown("Item clicked")
        // Dismiss dialog
        device.pressBack()
        waitForTextGone("Item clicked")
    }

    private fun MacrobenchmarkScope.waitForTextShown(text: String) {
        check(device.wait(Until.hasObject(By.text(text)), 500)) {
            "View showing '$text' not found after waiting 500 ms."
        }
    }

    private fun MacrobenchmarkScope.waitForTextGone(text: String) {
        check(device.wait(Until.gone(By.text(text)), 500)) {
            "View showing '$text' not found after waiting 500 ms."
        }
    }

    private fun MacrobenchmarkScope.clickOnText(text: String) {
        device
            .findObject(By.text(text))
            .click()
    }

    private fun MacrobenchmarkScope.clickOnId(resourceId: String) {
        device
            .findObject(By.res(packageName, resourceId))
            .click()
        // Chill to ensure we capture the end of the click span in the trace.
        Thread.sleep(100)
    }
}
