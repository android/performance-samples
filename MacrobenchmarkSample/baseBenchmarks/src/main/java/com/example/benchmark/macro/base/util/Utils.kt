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

package com.example.benchmark.macro.base.util

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import java.io.ByteArrayOutputStream

const val TARGET_PACKAGE = "com.example.macrobenchmark.target"
const val DEFAULT_ITERATIONS = 10

fun UiDevice.findObjectOrFail(selector: BySelector, dumpHierarchy: Boolean = true): UiObject2 {
    val element = findObject(selector)
    if (element == null) {
        org.junit.Assert.fail("Object not on screen ($selector)")
        if (dumpHierarchy) {
            dumpWindowHierarchy()
        }
    }
    return element
}


fun UiDevice.dumpWindowHierarchy(): String {
    val output = ByteArrayOutputStream()
    dumpWindowHierarchy(output)
    return output.toString()
}
