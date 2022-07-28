package com.example.jankstats.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.metrics.performance.PerformanceMetricsState

// [START metrics_state_holder]
/**
 * Retrieve MetricsStateHolder from compose and remember until the current view changes.
 */
@Composable
fun rememberMetricsStateHolder(): PerformanceMetricsState.Holder {
    val view = LocalView.current
    return remember(view) { PerformanceMetricsState.getHolderForHierarchy(view) }
}
// [END metrics_state_holder]
