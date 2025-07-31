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
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.macrobenchmark.target.R
import com.example.macrobenchmark.target.databinding.ActivityScrollViewBinding
import com.example.macrobenchmark.target.recyclerview.Entry
import com.example.macrobenchmark.target.util.ClickTrace

/**
 * An activity displaying a large ScrollView.
 */
class ScrollViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "ScrollView Sample"
        val binding = ActivityScrollViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.scrollcontent) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }

        val itemCount = intent.getIntExtra(RecyclerViewActivity.EXTRA_ITEM_COUNT, 1000)

        val items = List(itemCount) {
            Entry("Item $it")
        }

        val parent = binding.scrollcontent
        val inflater = LayoutInflater.from(parent.context)

        items.forEach { entry ->
            val itemView = inflater.inflate(R.layout.recycler_row, parent, false)
            parent.addView(itemView)
            val contentView = itemView.findViewById<TextView>(R.id.content)
            contentView.text = entry.contents
            itemView.setOnClickListener {
                ClickTrace.onClickPerformed()
                AlertDialog.Builder(this)
                    .setMessage("Item clicked")
                    .show()
            }
        }
    }
}