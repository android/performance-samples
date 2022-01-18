package com.example.macrobenchmark.target.nested_recycler

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.macrobenchmark.target.databinding.ActivityNestedRvBinding
import kotlinx.coroutines.flow.collect
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
    }
}

const val USE_RECYCLER_POOLS = "USE_RECYCLER_POOLS"
