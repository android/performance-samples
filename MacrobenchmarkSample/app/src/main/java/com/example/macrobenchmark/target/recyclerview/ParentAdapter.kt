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

package com.example.macrobenchmark.target.recyclerview

import android.content.Context
import android.graphics.Rect
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.tracing.Trace
import androidx.tracing.trace
import com.example.macrobenchmark.target.util.ClickTrace
import com.example.macrobenchmark.target.databinding.ItemParentBinding
import com.example.macrobenchmark.target.util.dp

class ParentAdapter(
    private val useRecyclerPools: Boolean
) : ListAdapter<ParentItem, BindingViewHolder<ItemParentBinding>>(ParentItemDiffCallback) {

    private val recyclerViewPool = if (useRecyclerPools) RecyclerView.RecycledViewPool() else null

    // allows keeping the state of nested RecyclerView after it's recycled
    private val nestedStates = mutableMapOf<Int, Parcelable?>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemParentBinding> {
        // adding custom trace to distinguish parent RecyclerView from child RecyclerView
        return trace("ParentAdapter.onCreateViewHolder") {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemParentBinding.inflate(inflater, parent, false)
            binding.rowTitle.setOnClickListener {
                ClickTrace.onClickPerformed()
            }

            binding.rowRecycler.addItemDecoration(childItemDecoration(parent.context))
            binding.rowRecycler.adapter = ChildAdapter()
            binding.rowRecycler.setRecycledViewPool(recyclerViewPool)

            val layoutManager = LinearLayoutManager(
                parent.context,
                RecyclerView.HORIZONTAL,
                false
            )

            layoutManager.recycleChildrenOnDetach = useRecyclerPools

            binding.rowRecycler.layoutManager = layoutManager

            BindingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemParentBinding>, position: Int) {
        // async section, because we want to check how long it took to submit the child list
        Trace.beginAsyncSection("ParentAdapter.onBindViewHolder", position)

        val item = getItem(position)
        holder.binding.rowTitle.text = item.content

        val adapter = (holder.binding.rowRecycler.adapter as ChildAdapter)

        adapter.submitList(item.children) {
            // retrieve and remove the nested state so it's used only once
            val restoredState = nestedStates.remove(position)

            if (restoredState != null) {
                // restore the previous state for item
                holder.binding.rowRecycler.layoutManager?.onRestoreInstanceState(restoredState)
            }

            Trace.endAsyncSection("ParentAdapter.onBindViewHolder", position)
        }
    }

    override fun onViewRecycled(holder: BindingViewHolder<ItemParentBinding>) {
        trace("ParentAdapter.onViewRecycled") {
            // save the state of nested recycler, so we can show the same scrolled position
            nestedStates[holder.adapterPosition] =
                holder.binding.rowRecycler.layoutManager?.onSaveInstanceState()
        }
        super.onViewRecycled(holder)
    }

    private fun childItemDecoration(context: Context) = object : RecyclerView.ItemDecoration() {
        private val marginsHorizontal = 16.dp(context).toInt()
        private val marginsVertical = 16.dp(context).toInt()

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val adapter = parent.adapter ?: return

            when (parent.getChildAdapterPosition(view)) {
                // first item has full margin from the left and half from the right
                0 -> outRect.set(
                    marginsHorizontal,
                    0,
                    marginsHorizontal / 2,
                    marginsVertical
                )

                // all but last has half margins
                adapter.itemCount - 1 -> outRect.set(
                    marginsHorizontal / 2,
                    0,
                    marginsHorizontal,
                    marginsVertical
                )

                // last has half margin from the left and full from the right
                else -> outRect.set(
                    marginsHorizontal / 2,
                    0,
                    marginsHorizontal / 2,
                    marginsVertical
                )
            }
        }
    }
}

