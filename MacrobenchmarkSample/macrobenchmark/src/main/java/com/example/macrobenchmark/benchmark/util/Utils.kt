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

package com.example.macrobenchmark.benchmark.util

import android.util.Log
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.SearchCondition
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import java.io.ByteArrayOutputStream

const val TARGET_PACKAGE = "com.example.macrobenchmark.target"
const val DEFAULT_ITERATIONS = 10

/**
 * Finds an element by [selector].
 * If not found, fails with [AssertionError] and dumps the window hierarchy.
 */
fun UiDevice.findOrFail(
    selector: BySelector,
    message: String? = null,
): UiObject2 {
    val element = findObject(selector)
    if (element == null) {
        val hierarchy = getWindowHierarchy()
        Log.d("Benchmark", hierarchy)
        throw AssertionError((message ?: "Object not on screen ($selector)") + "\n$hierarchy")
    }
    return element
}

/**
 * Waits until a [searchCondition] evaluates to true.
 * If not found within [timeout], fails with [AssertionError] and dumps the window hierarchy.
 * For example, wait [Until.hasObject] to wait until an element is present on screen.
 */
fun UiDevice.waitOrFail(
    searchCondition: SearchCondition<Boolean>,
    timeout: Long,
    message: String? = null,
) {
    if (!wait(searchCondition, timeout)) {
        val hierarchy = getWindowHierarchy()
        Log.d("Benchmark", hierarchy)
        throw AssertionError((message ?: "Object not on screen") + "\n$hierarchy")
    }
}

/**
 * Combines waiting for an element and returning it.
 * If an object is not present, it throws [AssertionError] and dumps the window hierarchy.
 */
fun UiDevice.waitAndFind(
    selector: BySelector,
    timeout: Long = 5_000,
    message: String? = null,
): UiObject2 {
    waitOrFail(Until.hasObject(selector), timeout, message)
    return findOrFail(selector, message)
}

/**
 * Simplifies dumping window hierarchy
 */
fun UiDevice.getWindowHierarchy(): String {
    val output = ByteArrayOutputStream()
    dumpWindowHierarchy(output)
    return output.toString()
}
