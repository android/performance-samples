/*
 * Copyright 2026 The Android Open Source Project
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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import com.example.jankstats.R
import com.example.jankstats.navigation.ComposeListRoute
import com.example.jankstats.navigation.MessageListRoute

@Composable
fun JankStatsScaffold(
    title: String,
    canNavigateUp: Boolean,
    currentTopLevelRoute: NavKey?,
    onNavigateUp: () -> Unit,
    onBottomTabSelected: (NavKey) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = if (canNavigateUp) {
                    {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_back),
                                contentDescription = "Navigate Up"
                            )
                        }
                    }
                } else null
            )
        },
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = currentTopLevelRoute == MessageListRoute,
                    onClick = { onBottomTabSelected(MessageListRoute) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_views),
                            contentDescription = stringResource(id = R.string.views)
                        )
                    },
                    label = { Text(text = stringResource(id = R.string.views)) }
                )
                BottomNavigationItem(
                    selected = currentTopLevelRoute == ComposeListRoute,
                    onClick = { onBottomTabSelected(ComposeListRoute) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_compose),
                            contentDescription = stringResource(id = R.string.compose)
                        )
                    },
                    label = { Text(text = stringResource(id = R.string.compose)) }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { /* No-op / Not implemented */ },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_construction),
                            contentDescription = stringResource(id = R.string.not_implemented)
                        )
                    },
                    label = { Text(text = stringResource(id = R.string.not_implemented)) }
                )
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
