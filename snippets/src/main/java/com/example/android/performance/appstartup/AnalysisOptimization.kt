/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.performance.appstartup

import android.app.Application
import android.os.StrictMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// [START android_performance_appstartup_strict_mode]
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // ...
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyDeath()
                    .build()
            )
        }
        // ...
    }
}
// [END android_performance_appstartup_strict_mode]

@Composable
private fun ConditionalCompositionSnippet() {
    // [START android_compose_performance_conditional_composition]
    var shouldLoad by remember { mutableStateOf(false) }

    if (shouldLoad) {
        MyComposable()
    }
    // [END android_compose_performance_conditional_composition]

    // [START android_compose_performance_conditional_composition_effect]
    LaunchedEffect(Unit) {
        shouldLoad = true
    }
    // [END android_compose_performance_conditional_composition_effect]
}

@Composable
private fun MyComposable() {
}

private object BuildConfig {
    const val DEBUG = true
}
