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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.LifecycleResumeEffect
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
 * This activity shows how to use JankStatsAggregator, a class in this test directory layered
 * on top of JankStats which aggregates the per-frame data. Instead of receiving jank data
 * per frame (which would happen by using JankStats directly), the report listener only
 * receives data when a report is issued, either when the activity goes into the background
 * or if JankStatsAggregator issues the report itself.
 */
// [START aggregator_activity_init]
class JankAggregatorActivity : ComponentActivity() {

    private lateinit var jankStatsAggregator: JankStatsAggregator

    // [START jank_aggregator_listener]
    private val jankReportListener =
        JankStatsAggregator.OnJankReportListener { reason, totalFrames, jankFrameData ->
            // A real app could do something more interesting, like writing the info to local storage and later on report it.

            Log.v(
                "JankStatsSample",
                "*** Jank Report ($reason), " +
                        "totalFrames = $totalFrames, " +
                        "jankFrames = ${jankFrameData.size}"
            )

            jankFrameData.forEach { frameData ->
                Log.v("JankStatsSample", frameData.toString())
            }
        }
    // [END jank_aggregator_listener]
    // [END_EXCLUDE]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            jankStatsAggregator = remember {
                JankStatsAggregator(window, jankReportListener)
            }
            // ...
            // metrics state holder can be retrieved regardless of JankStats initialization
            val metricsStateHolder = rememberMetricsStateHolder()
            // ...
            // ...
            // add activity name as state
            metricsStateHolder.state?.putState("Activity", javaClass.simpleName)

            LifecycleResumeEffect(jankStatsAggregator) {
                jankStatsAggregator.jankStats.isTrackingEnabled = true
                onPauseOrDispose {
                    jankStatsAggregator.issueJankReport("Activity paused")
                    jankStatsAggregator.jankStats.isTrackingEnabled = false
                }
            }

            val listState = rememberLazyListState()
            LaunchedEffect(metricsStateHolder, listState) {
                snapshotFlow { listState.isScrollInProgress }.collect { isScrolling ->
                    if (isScrolling) {
                        metricsStateHolder.state?.putState("LazyList", "Scrolling")
                    } else {
                        metricsStateHolder.state?.removeState("LazyList")
                    }
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
