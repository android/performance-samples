package com.example.macrobenchmark.target

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class ComposeActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This argument allows the Macrobenchmark tests control the content being tested.
        // In your app, you could use this approach to navigate to a consistent UI.
        // e.g. Here the UI is being populated with a well known number of list items.
        val itemCount = intent.getIntExtra(EXTRA_ITEM_COUNT, 1000)
        val data = entries(itemCount)

        setContent {
            MaterialTheme {
                LazyColumn(
                    Modifier.fillMaxSize()
                ) {
                    items(data, key = { it.contents }) { item ->
                        EntryRow(entry = item, Modifier.padding(8.dp))
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
private fun EntryRow(entry: Entry, modifier: Modifier = Modifier) {
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
