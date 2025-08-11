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
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiAutomatorTestScope
import androidx.test.uiautomator.textAsString
import androidx.test.uiautomator.uiAutomator
import androidx.test.uiautomator.watcher.PermissionDialog
import com.example.macrobenchmark.benchmark.util.DEFAULT_ITERATIONS
import com.example.macrobenchmark.benchmark.util.TARGET_PACKAGE
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
        benchmarkLoginActivity(
            extras = extras,
            measureBlock = {
                uiAutomator {
                    clickOnLogin()
                }
            }
        )
    }

    @Test
    fun loginInSetupBlock() {
        benchmarkLoginActivity(measureBlock = {
            uiAutomator {
                inputLoginDetails()
                clickOnLogin()
            }
        })
    }

    @Test
    fun loginWithUiAutomator() {
        benchmarkLoginActivity(
            measureBlock = {
                uiAutomator {
                    inputLoginDetails()
                    clickOnLogin()
                }
            }
        )
    }

    @Test
    fun loginInAfterPermissionsGranted() {
        benchmarkLoginActivity(measureBlock = {
            uiAutomator {
                watchFor(PermissionDialog) {
                    clickAllow()
                }
                inputLoginDetails()
                clickOnLogin()
            }
        })
    }

    private fun UiAutomatorTestScope.inputLoginDetails() {
        onElement { viewIdResourceName == "userName" }.text = "user"
        onElement { viewIdResourceName == "password" }.text = "password"
    }

    private fun UiAutomatorTestScope.clickOnLogin() =
        onElement { textAsString() == "Login" }.click()

    private fun benchmarkLoginActivity(
        extras: Bundle = Bundle(),
        setupBlock: MacrobenchmarkScope.() -> Unit = {},
        measureBlock: MacrobenchmarkScope.() -> Unit = {}
    ) {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(StartupTimingMetric(), FrameTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
            startupMode = StartupMode.COLD,
            iterations = DEFAULT_ITERATIONS,
            setupBlock = setupBlock,
        ) {
            uiAutomator {
                startIntent(
                    Intent()
                        .putExtras(extras)
                        .setAction("$packageName.LOGIN_ACTIVITY")
                )
            }
            measureBlock()
        }
    }
}