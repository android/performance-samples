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
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test

/**
 * This benchmark generates a basic baseline profile for the target package.
 * Refer to the [baseline profile documentation](https://developer.android.com/topic/performance/baselineprofiles)
 * for more information.
 *
 * You need to run the test on a rooted device/emulator. Generally emulator images without
 * Google Play Services are user-debug builds and support `adb root`.
 *
 * For the output Baseline Profile file, look at the folder
 * `macrobenchmark/build/outputs/connected_android_test_additional_output/benchmark/[device name]/`,
 * which contains the file [name-of-test]-baseline.prof.txt and copy the file into src/main/
 * (next to AndroidManifest.xml) and rename it to baseline-prof.txt.
 *
 * To filter out the generator when running benchmarks on CI, use instrumentation argument:
 * androidx.benchmark.enabledRules=Macrobenchmark
 * (see [documentation](https://android.devsite.corp.google.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args) for more info)
 */
@OptIn(ExperimentalBaselineProfilesApi::class)
class TrivialBaselineProfileBenchmark {
    // [START baseline_profile_basic]
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun appStartupOnly() =
        baselineProfileRule.collectBaselineProfile(packageName = TARGET_PACKAGE) {
            startActivityAndWait()
        }
    // [END baseline_profile_basic]

    /**
     * A more real world baseline profile collection method.
     * The collection starts at app startup and then goes through several user journeys.
     * This enables ahead of time compilation for these paths, making them smoother for users
     * from the first start.
     */
    @Test
    fun appStartupAndUserJourneys() {
        baselineProfileRule.collectBaselineProfile(packageName = TARGET_PACKAGE) {
            startActivityAndWait()
            with(device) {
                findObject(By.text("RECYCLERVIEW")).clickAndWait(Until.newWindow(), 500L)
                findObject(By.res(packageName, "recycler")).run {
                    fling(Direction.DOWN)
                    waitForIdle()
                    fling(Direction.UP)
                    pressBack()
                }
                findObject(By.text("COMPOSE LAZYLIST")).clickAndWait(Until.newWindow(), 500L)
                findObject(By.res("myLazyColumn")).run {
                    fling(Direction.DOWN)
                    waitForIdle()
                    fling(Direction.UP)
                    pressBack()
                }
            }
        }
    }
}
