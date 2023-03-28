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

import androidx.benchmark.macro.ExperimentalStableBaselineProfilesApi
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.macro.base.util.TARGET_PACKAGE
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A scaffold for creating a baseline profile user journey. Implementing classes can
 * start generating a profile directly by implementing [MacrobenchmarkScope.profileBlock].
 */
@RunWith(AndroidJUnit4::class)
abstract class BaselineProfileGeneratorScaffold {

    @get:Rule
    val rule = BaselineProfileRule()

    /**
     * Generate a baseline profile in this function.
     */
    abstract fun MacrobenchmarkScope.profileBlock()

    @OptIn(ExperimentalStableBaselineProfilesApi::class)
    @Test
    fun profileGenerator() {
        rule.collectStableBaselineProfile(
            packageName = TARGET_PACKAGE,
            maxIterations = 10
        ) {
            profileBlock()
        }
    }

}