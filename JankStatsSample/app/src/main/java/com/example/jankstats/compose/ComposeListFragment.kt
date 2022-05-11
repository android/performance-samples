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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.jankstats.R
import com.example.jankstats.tools.simulateJank
import kotlinx.coroutines.flow.collect

/**
 * Showcase how to work with JankStats from Compose.
  * This Fragment will intentionally cause poor UI performance which can be monitored by JankStats.
 */
class ComposeListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )

            setContent {
                MaterialTheme {
                    MessageList(onItemClick = {
                        findNavController().navigate(R.id.action_composeList_to_messageContent)
                    })
                }
            }
        }
    }
}

@Composable
fun MessageList(onItemClick: () -> Unit) {
    val listState = rememberLazyListState()
    val metricsStateHolder = rememberMetricsStateHolder()

    // Reporting scrolling state from compose should be done from side effect to prevent recomposition.
    LaunchedEffect(metricsStateHolder, listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { isScrolling ->
            if (isScrolling) {
                metricsStateHolder.state?.addState("LazyList", "Scrolling")
            } else {
                metricsStateHolder.state?.removeState("LazyList")
            }
        }
    }

    LazyColumn(state = listState) {
        items(100) { index ->
            MessageItem(index, onItemClick)
        }
    }
}


@Composable
fun MessageItem(item: Int, onItemClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Message #$item", Modifier.padding(end = 16.dp))
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
    MessageList(onItemClick = {})
}
