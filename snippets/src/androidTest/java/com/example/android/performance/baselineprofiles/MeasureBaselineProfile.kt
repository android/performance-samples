/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.android.performance.baselineprofiles

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.uiAutomator
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// [START android_performance_baselineprofiles_measure_compilation_modes]
@RunWith(AndroidJUnit4::class)
class ColdStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    // No ahead-of-time (AOT) compilation at all. Represents performance of a
    // fresh install on a user's device if you don't enable Baseline Profiles—
    // generally the worst case performance.
    @Test
    fun startupNoCompilation() = startup(CompilationMode.None())

    // Partial pre-compilation with Baseline Profiles. Represents performance of
    // a fresh install on a user's device.
    @Test
    fun startupPartialWithBaselineProfiles() =
        startup(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))

    // Partial pre-compilation with some just-in-time (JIT) compilation.
    // Represents performance after some app usage.
    @Test
    fun startupPartialCompilation() = startup(
        CompilationMode.Partial(
            baselineProfileMode = BaselineProfileMode.Disable,
            warmupIterations = 3
        )
    )

    // Full pre-compilation. Generally not representative of real user
    // experience, but can yield more stable performance metrics by removing
    // noise from JIT compilation within benchmark runs.
    @Test
    fun startupFullCompilation() = startup(CompilationMode.Full())

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.example.macrobenchmark.target",
        metrics = listOf(StartupTimingMetric()),
        compilationMode = compilationMode,
        iterations = 10,
        startupMode = StartupMode.COLD,
        setupBlock = {
            pressHome()
        }
    ) {
        uiAutomator {
            startApp(packageName)
            onElement(5_000) { viewIdResourceName == "my-content" }
        }
    }
}
// [END android_performance_baselineprofiles_measure_compilation_modes]
