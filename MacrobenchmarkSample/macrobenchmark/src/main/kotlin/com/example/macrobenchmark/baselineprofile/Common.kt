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

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.UiDevice

const val TARGET_PACKAGE = "com.example.macrobenchmark.target"

/**
 * Clears the application data for the package specified in the [MacrobenchmarkScope].
 * @param scope The [MacrobenchmarkScope] providing information about the benchmark,
 * including the package name of the app under test.
 */
fun UiDevice.clearData(scope: MacrobenchmarkScope) {
    val command = "pm clear $scope.packageName"
    val output = executeShellCommand(command)
//    Assert.assertEquals("Success", output)
}