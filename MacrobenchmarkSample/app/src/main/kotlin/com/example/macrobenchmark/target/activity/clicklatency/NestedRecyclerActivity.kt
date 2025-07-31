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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.macrobenchmark.target.databinding.ActivityNestedRvBinding
import com.example.macrobenchmark.target.recyclerview.NestedRecyclerViewModel
import com.example.macrobenchmark.target.recyclerview.ParentAdapter
import kotlinx.coroutines.launch

class NestedRecyclerActivity : AppCompatActivity() {

    private val viewModel by viewModels<NestedRecyclerViewModel>()

    private lateinit var binding: ActivityNestedRvBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val useRecyclerPools = intent.getBooleanExtra(USE_RECYCLER_POOLS, false)

        binding = ActivityNestedRvBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val adapter = ParentAdapter(useRecyclerPools)

        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect {
                    adapter.submitList(it)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.recycler) { v, insets ->
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
    }
}

const val USE_RECYCLER_POOLS = "USE_RECYCLER_POOLS"
