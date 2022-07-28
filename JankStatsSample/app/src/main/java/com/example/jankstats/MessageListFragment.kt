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

package com.example.jankstats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.metrics.performance.PerformanceMetricsState
import androidx.recyclerview.widget.RecyclerView
import com.example.jankstats.databinding.FragmentMessageListBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class MessageListFragment : Fragment() {

    private var _binding: FragmentMessageListBinding? = null
    private val binding get() = requireNotNull(_binding)

    // initialized when View is created
    private var metricsStateHolder: PerformanceMetricsState.Holder? = null

    private val messageList = Array(100) { "Message #$it" }

    // [START state_recyclerview]
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            // check if JankStats is initialized and skip adding state if not
            val metricsState = metricsStateHolder?.state ?: return

            when (newState) {
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    metricsState.putState("RecyclerView", "Dragging")
                }
                RecyclerView.SCROLL_STATE_SETTLING -> {
                    metricsState.putState("RecyclerView", "Settling")
                }
                else -> {
                    metricsState.removeState("RecyclerView")
                }
            }
        }
    }
    // [END state_recyclerview]

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        metricsStateHolder = PerformanceMetricsState.getHolderForHierarchy(view)

        binding.messageList.adapter = MessageListAdapter(messageList)
        binding.messageList.addOnScrollListener(scrollListener)
    }

    override fun onDestroyView() {
        _binding = null
        metricsStateHolder = null
        super.onDestroyView()
    }

}
