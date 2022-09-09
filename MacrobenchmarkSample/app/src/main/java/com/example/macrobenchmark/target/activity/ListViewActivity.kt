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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.macrobenchmark.target.util.ClickTrace
import com.example.macrobenchmark.target.recyclerview.Entry
import com.example.macrobenchmark.target.databinding.ActivityListViewBinding

/**
 * An activity displaying a large ListView.
 */
class ListViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "ListView Sample"
        val binding = ActivityListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val itemCount = intent.getIntExtra(RecyclerViewActivity.EXTRA_ITEM_COUNT, 1000)

        val items = List(itemCount) {
            Entry("Item $it")
        }

        binding.listview.adapter = object : BaseAdapter() {
            override fun getCount() = itemCount

            override fun getItem(position: Int) = items[position]

            override fun getItemId(position: Int) = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val itemView = if (convertView == null) {
                    val inflater = LayoutInflater.from(parent.context)
                    inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                } else {
                    convertView
                }

                val contentView = itemView.findViewById<TextView>(android.R.id.text1)

                contentView.text = getItem(position).contents
                return contentView
            }
        }

        binding.listview.setOnItemClickListener { _, _, _, _ ->
            ClickTrace.onClickPerformed()
            AlertDialog.Builder(this)
                    .setMessage("Item clicked")
                    .show()
        }

    }
}