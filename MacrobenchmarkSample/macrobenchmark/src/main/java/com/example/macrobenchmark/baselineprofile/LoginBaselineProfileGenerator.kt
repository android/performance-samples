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
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By

class LoginBaselineProfileGenerator : BaselineProfileGeneratorScaffold() {

    override fun MacrobenchmarkScope.profileBlock() {
        startActivityAndWait(Intent("$packageName.LOGIN_ACTIVITY"))
        device.findObject(By.res("userName")).text = "user"
        device.findObject(By.res("password")).text = "password"
        device.findObject(By.res("login")).click()
        device.waitForIdle()
    }
}
