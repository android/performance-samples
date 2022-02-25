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

package com.example.macrobenchmark.startup

import android.content.Intent
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.filters.LargeTest
import com.example.macrobenchmark.TARGET_PACKAGE
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

private const val ITERATIONS = 5
private const val LIST_ITEMS = 5

@LargeTest
@RunWith(Parameterized::class)
class SmallListStartupBenchmark(
    private val startupMode: StartupMode
) {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        iterations = ITERATIONS,
        startupMode = startupMode,
    ) {
        val intent = Intent("$packageName.RECYCLER_VIEW_ACTIVITY")
        intent.putExtra("ITEM_COUNT", LIST_ITEMS)

        startActivityAndWait(intent)
    }

    @Test
    fun startupCompose() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        iterations = ITERATIONS,
        startupMode = startupMode,
    ) {
        val intent = Intent("$packageName.COMPOSE_ACTIVITY")
        intent.putExtra("ITEM_COUNT", LIST_ITEMS)

        startActivityAndWait(intent)
    }

    companion object {
        @Parameterized.Parameters(name = "mode={0}")
        @JvmStatic
        fun parameters(): List<Array<Any>> {
            return listOf(
                StartupMode.COLD,
                StartupMode.WARM
            ).map { arrayOf(it) }
        }
    }
}
