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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.macrobenchmark.target.util.ClickTrace
import com.example.macrobenchmark.target.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.launchRecyclerActivity.setOnClickListener {
            ClickTrace.onClickPerformed()
            val intent = Intent(it.context, NonExportedRecyclerActivity::class.java)
            startActivity(intent)
        }

        binding.launchListViewActivity.setOnClickListener {
            ClickTrace.onClickPerformed()
            val intent = Intent(it.context, ListViewActivity::class.java)
            startActivity(intent)
        }

        binding.launchScrollViewActivity.setOnClickListener {
            ClickTrace.onClickPerformed()
            val intent = Intent(it.context, ScrollViewActivity::class.java)
            startActivity(intent)
        }

        binding.launchComposeList.setOnClickListener {
            ClickTrace.onClickPerformed()
            val intent = Intent(it.context, ComposeActivity::class.java)
            startActivity(intent)
        }

        binding.nestedRecyclerActivity.setOnClickListener {
            ClickTrace.onClickPerformed()
            val intent = Intent(it.context, NestedRecyclerActivity::class.java)
            startActivity(intent)
        }

        binding.nestedRecyclerWithPoolsActivity.setOnClickListener {
            ClickTrace.onClickPerformed()
            val intent = Intent(it.context, NestedRecyclerActivity::class.java).apply {
                putExtra(USE_RECYCLER_POOLS, true)
            }
            startActivity(intent)
        }
    }
}
