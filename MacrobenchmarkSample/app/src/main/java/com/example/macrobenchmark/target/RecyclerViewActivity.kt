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

package com.example.macrobenchmark.target

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class RecyclerViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "RecyclerView Sample"
        setContentView(R.layout.activity_recycler_view)
        val recycler = findViewById<RecyclerView>(R.id.recycler)
        // This argument allows the Macrobenchmark tests control the content being tested.
        // In your app, you could use this approach to navigate to a consistent UI.
        // e.g. Here the UI is being populated with a well known number of list items.
        val itemCount = intent.getIntExtra(EXTRA_ITEM_COUNT, 1000)
        val adapter = EntryAdapter(entries(itemCount))
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    private fun entries(size: Int) = List(size) {
        Entry("Item $it")
    }

    companion object {
        const val EXTRA_ITEM_COUNT = "ITEM_COUNT"
    }
}
