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

package com.example.macrobenchmark.benchmark

import android.content.Intent
import android.os.Bundle
import androidx.benchmark.ExperimentalBenchmarkConfigApi
import androidx.benchmark.ExperimentalConfig
import androidx.benchmark.StartupInsightsConfig
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.benchmark.perfetto.ExperimentalPerfettoCaptureApi
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import com.example.macrobenchmark.benchmark.permissions.allowNotifications
import com.example.macrobenchmark.benchmark.util.DEFAULT_ITERATIONS
import com.example.macrobenchmark.benchmark.util.TARGET_PACKAGE
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalBenchmarkConfigApi::class, ExperimentalPerfettoCaptureApi::class)
@RunWith(AndroidJUnit4::class)
class LoginBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun loginByIntent() {
        val extras = Bundle().apply {
            putString("user", "benchUser")
            putString("password", "benchPassword")
        }
        benchmarkLoginActivity(extras = extras)
    }

    @Test
    fun loginInSetupBlock() {
        benchmarkLoginActivity(setupBlock = {
            startActivityAndWait(Intent("$packageName.LOGIN_ACTIVITY"))
            login()
        })
    }

    @Test
    fun loginWithUiAutomator() {
        benchmarkLoginActivity {
            login()
        }
    }

    @Test
    fun loginInAfterPermissionsGranted() {
        benchmarkLoginActivity(setupBlock = {
            allowNotifications()

            startActivityAndWait(Intent("$packageName.LOGIN_ACTIVITY"))
            login()
        })
    }

    private fun MacrobenchmarkScope.login() {
        device.findObject(By.res("userName")).text = "user"
        device.findObject(By.res("password")).text = "password"
        device.findObject(By.res("login")).click()
        device.waitForIdle()
    }

    private fun benchmarkLoginActivity(
        extras: Bundle = Bundle(),
        setupBlock: MacrobenchmarkScope.() -> Unit = {},
        measureBlock: MacrobenchmarkScope.() -> Unit = {}
    ) {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(StartupTimingMetric(), FrameTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
            experimentalConfig = ExperimentalConfig(startupInsightsConfig = StartupInsightsConfig(true)),
            startupMode = StartupMode.COLD,
            iterations = DEFAULT_ITERATIONS,
            setupBlock = setupBlock,
        ) {
            startActivityAndWait(
                Intent()
                    .putExtras(extras)
                    .setAction("$packageName.LOGIN_ACTIVITY")
            )
            measureBlock()
        }
    }
}