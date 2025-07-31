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

package com.example.macrobenchmark.target.activity.clicklatency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tracing.trace
import com.example.macrobenchmark.target.recyclerview.Entry
import com.example.macrobenchmark.target.util.ClickTrace

@OptIn(ExperimentalComposeUiApi::class)
class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This argument allows the Macrobenchmark tests control the content being tested.
        // In your app, you could use this approach to navigate to a consistent UI.
        // e.g. Here the UI is being populated with a well known number of list items.
        val itemCount = intent.getIntExtra(EXTRA_ITEM_COUNT, 1000)
        val data = entries(itemCount)

        setContent {
            BenchmarkComposeList(data)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun BenchmarkComposeList(data: List<Entry>) {
        MaterialTheme {
            var value by remember { mutableStateOf("Enter text here") }
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(title = { Text("Compose Sample") })
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = value,
                    onValueChange = { value = it },
                    placeholder = { Text("Enter text here") }
                )

                LazyColumn {
                    items(data, key = { it.contents }) { item ->
                        EntryRow(
                            entry = item,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    ClickTrace.onClickPerformed()
                                    AlertDialog
                                        .Builder(this@ComposeActivity)
                                        .setMessage("Item clicked")
                                        .show()
                                }
                        )
                    }
                }
            }
        }
    }

    private fun entries(size: Int) = List(size) {
        Entry("Item $it")
    }

    companion object {
        const val EXTRA_ITEM_COUNT = "ITEM_COUNT"
    }
}

@Composable
private fun EntryRow(entry: Entry, modifier: Modifier = Modifier) = trace("EntryRowCustomTrace") {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = entry.contents,
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize()
            )

            Spacer(modifier = Modifier.weight(1f))

            Checkbox(
                checked = false,
                onCheckedChange = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun EntryRowPreview() {
    val entry = Entry("Sample text")
    EntryRow(entry = entry, Modifier.padding(8.dp))
}
