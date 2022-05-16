package com.example.jankstats.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.metrics.performance.PerformanceMetricsState

// [START rememberMetricsStateHolder]
/**
 * Retrieve MetricsStateHolder from compose and remember until the current view changes.
 */
@Composable
fun rememberMetricsStateHolder(): PerformanceMetricsState.MetricsStateHolder {
    val view = LocalView.current
    return remember(view) { PerformanceMetricsState.getForHierarchy(view) }
}
// [END rememberMetricsStateHolder]