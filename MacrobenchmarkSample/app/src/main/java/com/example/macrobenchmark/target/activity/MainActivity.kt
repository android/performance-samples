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

package com.example.macrobenchmark.target.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.macrobenchmark.target.activity.clicklatency.ComposeActivity
import com.example.macrobenchmark.target.activity.clicklatency.ListViewActivity
import com.example.macrobenchmark.target.activity.clicklatency.NestedRecyclerActivity
import com.example.macrobenchmark.target.activity.clicklatency.NonExportedRecyclerActivity
import com.example.macrobenchmark.target.activity.clicklatency.ScrollViewActivity
import com.example.macrobenchmark.target.activity.clicklatency.USE_RECYCLER_POOLS
import com.example.macrobenchmark.target.activity.login.LoginActivity
import com.example.macrobenchmark.target.util.ClickTrace

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                TopAppBar(title = { Text(text = "Benchmark Sample Target App") })
                ActivityList()
            }
        }
    }


    @Composable
    fun ActivityList() {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = "This app runs macrobenchmark tests",
            textAlign = TextAlign.Center
        )
        Column {
            BenchmarkActivityButton(name = "Login") {
                launchActivityWithTrace<LoginActivity>()
            }
            BenchmarkActivityButton(name = "ListView") {
                launchActivityWithTrace<ListViewActivity>()
            }
            BenchmarkActivityButton(name = "Compose") {
                launchActivityWithTrace<ComposeActivity>()
            }
            BenchmarkActivityButton(name = "ScrollView") {
                launchActivityWithTrace<ScrollViewActivity>()
            }
            BenchmarkActivityButton(name = "Fully Drawn") {
                launchActivityWithTrace<FullyDrawnStartupActivity>()
            }
            BenchmarkActivityButton(name = "RecyclerView") {
                launchActivityWithTrace<NonExportedRecyclerActivity>()
            }
            BenchmarkActivityButton(name = "Nested RecyclerView") {
                launchActivityWithTrace<NestedRecyclerActivity>()
            }
            BenchmarkActivityButton(name = "Nested RecyclerView with Pools") {
                launchActivityWithTrace<NestedRecyclerActivity>(
                    Intent().putExtra(
                        USE_RECYCLER_POOLS, true
                    )
                )
            }
        }
    }

    @Composable
    fun BenchmarkActivityButton(name: String, onClick: () -> Unit) {
        TextButton(
            modifier = Modifier.padding(8.dp),
            onClick = onClick,
            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
        ) {
            Text(name)
        }
    }

    private inline fun <reified T : Activity> launchActivityWithTrace(base: Intent? = null) {
        ClickTrace.onClickPerformed()
        val intent = Intent(this, T::class.java)
        if (base != null) {
            intent.putExtras(base)
        }
        startActivity(intent)
    }
}