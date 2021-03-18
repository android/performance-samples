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

import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@LargeTest
@RunWith(Parameterized::class)
class BitmapStartupBenchmark(private val startupMode: StartupMode, private val bitmapId: Int) {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() = benchmarkRule.measureStartup(
        profileCompiled = false,
        startupMode = startupMode,
        iterations = 3
    ) {
        action = "com.example.macrobenchmark.target.BITMAP_ACTIVITY"
        putExtra("EXTRAS_IMAGE_ID", bitmapId)
    }

    companion object {
        @Parameterized.Parameters(name = "mode={0}, bitmap={1}")
        @JvmStatic
        fun parameters(): List<Array<Any>> {
            return listOf(
                arrayOf(StartupMode.COLD, 0 /* small image */),
                arrayOf(StartupMode.COLD, 1 /* large image */)
            )
        }
    }
}
