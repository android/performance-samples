// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.android.perf.test

import android.content.Intent
import android.content.pm.PackageManager
import android.support.test.InstrumentationRegistry
import android.support.test.filters.MediumTest
import android.support.test.filters.SdkSuppress
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiObject2
import android.support.test.uiautomator.Until
import org.hamcrest.core.IsNull.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * UiAutomator tests that triggers measure/layouts passes in the MainActivity app to compare the
 * UI performance for ConstraintLayout in comparison to traditional layouts.
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
@MediumTest
class PerformanceTest {

    private lateinit var device: UiDevice

    @Before
    fun startMainActivityFromHomeScreen() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        val launcherPackage = launcherPackageName
        assertThat(launcherPackage, notNullValue())
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT)

        val context = InstrumentationRegistry.getTargetContext()
        val intent = context.packageManager
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)    // Clear out any previous instances

        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT)
    }

    @Test
    @Throws(Throwable::class)
    fun testRunCalculationTraditionalLayouts() {
        runCalculation("button_start_calc_traditional")
    }

    @Test
    @Throws(Throwable::class)
    fun testRunCalculationConstraintLayout() {
        runCalculation("button_start_calc_constraint")
    }

    /**
     * Runs the calculation on a connected device or on an emulator. By clicking a button in the
     * app, the app runs measure/layout passes specific times.
     */
    private fun runCalculation(buttonIdToStart: String) {
        device.findObject(By.res(BASIC_SAMPLE_PACKAGE, buttonIdToStart)).click()
        device.wait<UiObject2>(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE,
                "textview_finish")), TimeUnit.SECONDS.toMillis(15))
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    private val launcherPackageName: String
        get() {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)

            val pm = InstrumentationRegistry.getContext().packageManager
            val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            return resolveInfo.activityInfo.packageName
        }

    companion object {
        private val BASIC_SAMPLE_PACKAGE = "com.example.android.perf"
        private val LAUNCH_TIMEOUT = 5000L
    }
}
