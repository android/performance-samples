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

package com.example.android.performance.vitals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// [START android_performance_launch_time_fully_drawn_reporter]
class MainActivity : ComponentActivity() {

    sealed interface ActivityState {
        data object LOADING : ActivityState
        data object LOADED : ActivityState
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var activityState by remember {
                mutableStateOf(ActivityState.LOADING as ActivityState)
            }
            fullyDrawnReporter.addOnReportDrawnListener {
                activityState = ActivityState.LOADED
            }
            ReportFullyDrawnTheme {
                when (activityState) {
                    is ActivityState.LOADING -> {
                        // Display the loading UI.
                    }
                    is ActivityState.LOADED -> {
                        // Display the full UI.
                    }
                }
            }
            SideEffect {
                fullyDrawnReporter.addReporter()
                lifecycleScope.launch(Dispatchers.IO) {
                    // Perform the background operation.
                    fullyDrawnReporter.removeReporter()
                }
                fullyDrawnReporter.addReporter()
                lifecycleScope.launch(Dispatchers.IO) {
                    // Perform the background operation.
                    fullyDrawnReporter.removeReporter()
                }
            }
        }
    }
}
// [END android_performance_launch_time_fully_drawn_reporter]

@Composable
private fun ReportFullyDrawnTheme(content: @Composable () -> Unit) {
    content()
}
