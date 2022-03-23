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

import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalBaselineProfilesApi::class)
// Remove @Ignore to actually run the test using a rooted device/emulator.
// Generally emulator images without Google Play Services are user-debug builds, which support
// `adb root`

// For the output baseline profile file, look at the `Benchmark` tag in logcat for the location
// of the output directory.
//@Ignore
/**
 * This benchmark generates a basic baseline profile for the target package.
 * Refer to the [baseline profile documentation](https://developer.android.com/studio/profile/baselineprofiles)
 * for more information.
 */
class TrivialBaselineProfileBenchmark {
    // [START baseline_profile_basic]
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startup() = baselineProfileRule.collectBaselineProfile(
        packageName = TARGET_PACKAGE,
        profileBlock = {
            startActivityAndWait()
            device.waitForIdle()
        }
    )
    // [END baseline_profile_basic]
}
