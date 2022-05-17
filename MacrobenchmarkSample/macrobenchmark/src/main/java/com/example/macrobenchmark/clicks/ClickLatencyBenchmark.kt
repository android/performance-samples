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
                    } else {
                        device.pressBack()
                    }
                    Thread.sleep(500)
                }
        ) {
            clickOnId("launchRecyclerActivity")
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
                    Thread.sleep(500)
                }
        ) {
            clickOnText("Item 0")
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
                        Thread.sleep(500)
                    }
                }
        ) {
            clickOnText("Item 0")
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
                        Thread.sleep(500)
                    }
                }
        ) {
            clickOnText("Item 0")
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
                        Thread.sleep(500)
                    }
                }
        ) {
            clickOnText("Item 0")
        }
    }

    private fun MacrobenchmarkScope.clickOnText(text: String) {
        device
                .findObject(By.text(text))
                .click()
        // Chill to ensure we capture the end of the click span in the trace.
        Thread.sleep(100)
    }

    private fun MacrobenchmarkScope.clickOnId(resourceId: String) {
        device
                .findObject(By.res(packageName, resourceId))
                .click()
        // Chill to ensure we capture the end of the click span in the trace.
        Thread.sleep(100)
    }
}
