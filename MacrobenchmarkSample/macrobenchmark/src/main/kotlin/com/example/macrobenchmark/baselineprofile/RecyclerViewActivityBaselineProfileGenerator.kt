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
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.uiAutomator
import com.example.macrobenchmark.benchmark.util.TARGET_PACKAGE
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Ignore // TODO flinging not working, ignore for now to test the pipeline
@RunWith(AndroidJUnit4ClassRunner::class)
class RecyclerViewActivityBaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        rule.collect(
            packageName = TARGET_PACKAGE,
            maxIterations = 15,
            stableIterations = 3
        ) {
            uiAutomator {
                // Start into the RecyclerViewActivity
                startIntent(Intent("$TARGET_PACKAGE.RECYCLER_VIEW_ACTIVITY"))

                // Scrolling RecyclerView journey
                onElement { viewIdResourceName == "recycler" }.fling(Direction.DOWN)
                onElement { viewIdResourceName == "recycler" }.fling(Direction.UP)
            }
        }
    }
}
