/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.macrobenchmark.baselineprofile

import android.content.Intent
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import com.example.macrobenchmark.benchmark.util.findOrFail
import com.example.macrobenchmark.benchmark.util.waitAndFind
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Ignore // TODO causing stale object excpetion on CI .. why?
@RunWith(AndroidJUnit4ClassRunner::class)
class ComposeActivityBaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        rule.collect(
            packageName = TARGET_PACKAGE,
            maxIterations = 15,
            stableIterations = 3
        ) {
            // Start into the Compose Activity
            startActivityAndWait(Intent("$TARGET_PACKAGE.COMPOSE_ACTIVITY"))

            // Scrolling through the Compose journey
            device.waitAndFind(By.res("myLazyColumn")).also {
                it.setGestureMargin(device.displayWidth / 10)
                it.fling(Direction.DOWN)
            }

            device.findOrFail(By.res("myLazyColumn")).fling(Direction.UP)
        }
    }
}
