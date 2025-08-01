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

package com.example.macrobenchmark.benchmark.clickslatency;

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.UiAutomatorTestScope
import androidx.test.uiautomator.uiAutomator
import com.example.macrobenchmark.benchmark.util.DEFAULT_ITERATIONS
import com.example.macrobenchmark.benchmark.util.TARGET_PACKAGE
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(TraceSectionMetric("ClickTrace")),
            compilationMode = CompilationMode.Full(),
            startupMode = null,
            iterations = DEFAULT_ITERATIONS,
        ) {
            uiAutomator {
                startApp()
                val title = "RecyclerView Sample"
                onView { viewIdResourceName == "launchRecyclerActivity" }.click()
                onView { text == title && isVisibleToUser }
                pressBack()
                onView { text == title && !isVisibleToUser }
            }
        }
    }

    @Test
    fun recyclerViewClick() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(TraceSectionMetric("ClickTrace")),
            compilationMode = CompilationMode.Full(),
            startupMode = null,
            iterations = DEFAULT_ITERATIONS,
        ) {
            uiAutomator {
                startIntent(Intent("$packageName.RECYCLER_VIEW_ACTIVITY"))
                clickOnFirstItem()
            }
        }
    }

    @Test
    fun composeLazyColumnClick() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(TraceSectionMetric("ClickTrace")),
            compilationMode = CompilationMode.Full(),
            startupMode = null,
            iterations = DEFAULT_ITERATIONS,
        ) {
            uiAutomator {
                startIntent(Intent("$packageName.COMPOSE_ACTIVITY"))
                clickOnFirstItem()
            }
        }
    }

    @Test
    fun listViewClick() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(TraceSectionMetric("ClickTrace")),
            compilationMode = CompilationMode.Full(),
            startupMode = null,
            iterations = DEFAULT_ITERATIONS,
        ) {
            uiAutomator {
                startIntent(Intent("$packageName.LIST_VIEW_ACTIVITY"))
                clickOnFirstItem()
            }
        }
    }

    @Test
    fun scrollViewClick() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(TraceSectionMetric("ClickTrace")),
            compilationMode = CompilationMode.Full(),
            startupMode = null,
            iterations = DEFAULT_ITERATIONS,
        ) {
            uiAutomator {
                startIntent(Intent("$packageName.SCROLL_VIEW_ACTIVITY"))
                clickOnFirstItem()
            }
        }
    }

    private fun UiAutomatorTestScope.clickOnFirstItem() {
        onView { text == "Item 0" }.click()
        onView { text == "Item clicked" && isVisibleToUser }
        pressBack()
        onView { text == "Item clicked" && !isVisibleToUser }
    }
}

