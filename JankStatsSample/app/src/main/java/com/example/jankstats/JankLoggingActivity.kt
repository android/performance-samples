/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jankstats

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.PerformanceMetricsState
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.jankstats.compose.JankStatsScaffold
import com.example.jankstats.compose.JankStatsTheme
import com.example.jankstats.compose.MessageContentScreen
import com.example.jankstats.compose.MessageList
import com.example.jankstats.compose.rememberMetricsStateHolder
import com.example.jankstats.navigation.ComposeListRoute
import com.example.jankstats.navigation.MessageContentRoute
import com.example.jankstats.navigation.MessageListRoute
import com.example.jankstats.navigation.Navigator
import com.example.jankstats.navigation.rememberNavigationState
import com.example.jankstats.navigation.toEntries

/**
 * This activity shows the basic usage of JankStats, from creating and enabling it to track
 * a view hierarchy, to setting application state on JankStats, to receiving and logging per-frame
 * callbacks with jank data.
 */
class JankLoggingActivity : ComponentActivity() {

    private lateinit var jankStats: JankStats

    private val jankFrameListener = JankStats.OnFrameListener { frameData ->
        // A real app could do something more interesting, like writing the info to local storage and later on report it.
        Log.v("JankStatsSample", frameData.toString())
    }
    // [END jank_frame_listener]
    // [END_EXCLUDE]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            jankStats = remember {
                JankStats.createAndTrack(window, jankFrameListener)
            }
            // ...
            // metrics state holder can be retrieved regardless of JankStats initialization
            val metricsStateHolder = rememberMetricsStateHolder()
            // ...
            // ...
            // add activity name as state
            metricsStateHolder.state?.putState("Activity", javaClass.simpleName)

            LifecycleResumeEffect(jankStats) {
                jankStats.isTrackingEnabled = true
                onPauseOrDispose {
                    Log.e("Activity Paused,","Tracking")
                    jankStats.isTrackingEnabled = false
                }
            }

            val topLevelRoutes = remember { setOf<NavKey>(MessageListRoute, ComposeListRoute) }
            val navigationState = rememberNavigationState(
                startRoute = MessageListRoute,
                topLevelRoutes = topLevelRoutes
            )
            val navigator = remember(navigationState) { Navigator(navigationState) }

            val currentStack = navigationState.backStacks[navigationState.topLevelRoute]
            val activeKey = currentStack?.lastOrNull() ?: navigationState.topLevelRoute
            val canNavigateUp = currentStack != null && currentStack.size > 1

            val title = when (activeKey) {
                is MessageListRoute -> "Message List"
                is ComposeListRoute -> "Compose List"
                is MessageContentRoute -> "Message Content"
                else -> "JankStats Sample"
            }

            val entryProvider = remember(navigator) {
                entryProvider {
                    entry<MessageListRoute> {
                        MessageList(onItemClick = { headerText ->
                            navigator.navigate(MessageContentRoute(headerText))
                        })
                    }
                    entry<ComposeListRoute> {
                        MessageList(onItemClick = { headerText ->
                            navigator.navigate(MessageContentRoute(headerText))
                        })
                    }
                    entry<MessageContentRoute> { route ->
                        MessageContentScreen(title = route.title)
                    }
                }
            }

            JankStatsTheme {
                JankStatsScaffold(
                    title = title,
                    canNavigateUp = canNavigateUp,
                    currentTopLevelRoute = navigationState.topLevelRoute,
                    onNavigateUp = { navigator.goBack() },
                    onBottomTabSelected = { navKey ->
                        navigator.navigate(navKey)
                    }
                ) { innerPadding ->
                    NavDisplay(
                        entries = navigationState.toEntries(entryProvider),
                        onBack = { navigator.goBack() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
