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

package com.example.macrobenchmark

import android.content.Intent
import android.os.Bundle
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup_loginByIntent() {
        val extras = Bundle().apply {
            putString("user", "benchUser")
            putString("password", "benchPassword")
        }
        benchmarkLoginActivity(extras = extras)
    }

    @Test
    fun startup_loginInSetupBlock() {
        benchmarkLoginActivity(setupBlock = {
            startActivityAndWait(Intent("$packageName.LOGIN_ACTIVITY"))
            login()
        })
    }

    @Test
    fun statup_loginWithUiAutomator() {
        benchmarkLoginActivity {
            login()
        }
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
            metrics = listOf(StartupTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
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