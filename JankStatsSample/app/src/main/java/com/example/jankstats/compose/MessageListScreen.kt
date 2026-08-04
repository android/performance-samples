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

package com.example.jankstats.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jankstats.tools.simulateJank

@Composable
fun MessageList(
    onItemClick: (String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val listState = rememberLazyListState()
    // [START compose_jank_metrics]
    val metricsStateHolder = rememberMetricsStateHolder()

    // Reporting scrolling state from compose should be done from side effect to prevent recomposition.
    LaunchedEffect(metricsStateHolder, listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { isScrolling ->
            if (isScrolling) {
                metricsStateHolder.state?.putState("LazyList", "Scrolling")
            } else {
                metricsStateHolder.state?.removeState("LazyList")
            }
        }
    }
    // [END compose_jank_metrics]

    LazyColumn(
        state = listState,
        contentPadding = contentPadding
    ) {
        items(100) { index ->
            MessageItem(index, onItemClick)
        }
    }
}

@Composable
fun MessageItem(item: Int, onItemClick: (String) -> Unit) {
    val headerText = "Message #$item"
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = { onItemClick(headerText) })
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(headerText, Modifier.padding(end = 16.dp))
        JankyComposable()
    }
}

@Composable
fun JankyComposable() {
    Box(
        Modifier
            .width(20.dp)
            .height(20.dp)
            .background(Color.Red)
    ) {
        simulateJank()
    }
}

@Preview(widthDp = 500)
@Composable
fun MessageListPreview() {
    JankStatsTheme {
        MessageList(onItemClick = {})
    }
}
