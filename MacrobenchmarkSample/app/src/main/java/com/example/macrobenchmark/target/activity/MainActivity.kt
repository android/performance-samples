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
import androidx.appcompat.app.AppCompatActivity
import com.example.macrobenchmark.target.activity.clicklatency.ComposeActivity
import com.example.macrobenchmark.target.activity.clicklatency.ListViewActivity
import com.example.macrobenchmark.target.activity.clicklatency.NestedRecyclerActivity
import com.example.macrobenchmark.target.activity.clicklatency.NonExportedRecyclerActivity
import com.example.macrobenchmark.target.activity.clicklatency.ScrollViewActivity
import com.example.macrobenchmark.target.activity.clicklatency.USE_RECYCLER_POOLS
import com.example.macrobenchmark.target.activity.login.LoginActivity
import com.example.macrobenchmark.target.databinding.ActivityMainBinding
import com.example.macrobenchmark.target.util.ClickTrace

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.launchLoginActivity.setOnClickListener {
            launchActivityWithTrace<LoginActivity>()
        }

        binding.launchFullyDrawnActivity.setOnClickListener {
            launchActivityWithTrace<FullyDrawnStartupActivity>()
        }

        binding.launchRecyclerActivity.setOnClickListener {
            launchActivityWithTrace<NonExportedRecyclerActivity>()
        }

        binding.launchListViewActivity.setOnClickListener {
            launchActivityWithTrace<ListViewActivity>()
        }

        binding.launchScrollViewActivity.setOnClickListener {
            launchActivityWithTrace<ScrollViewActivity>()
        }

        binding.launchComposeList.setOnClickListener {
            launchActivityWithTrace<ComposeActivity>()
        }

        binding.nestedRecyclerActivity.setOnClickListener {
            launchActivityWithTrace<NestedRecyclerActivity>()
        }

        binding.nestedRecyclerWithPoolsActivity.setOnClickListener {
            launchActivityWithTrace<NestedRecyclerActivity>(
                Intent().putExtra(
                    USE_RECYCLER_POOLS, true
                )
            )
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
