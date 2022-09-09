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

import androidx.recyclerview.widget.DiffUtil

data class ParentItem(
    val id: String,
    val content: String,
    val children: List<ChildItem>
)

object ParentItemDiffCallback : DiffUtil.ItemCallback<ParentItem>() {
    override fun areItemsTheSame(oldItem: ParentItem, newItem: ParentItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ParentItem, newItem: ParentItem): Boolean = when {
        oldItem.content != newItem.content -> false
        // go through the children and find whether are the same
        !oldItem.children
            .asSequence()
            .zip(newItem.children.asSequence())
            .all { (a, b) -> ChildItemDiffCallback.areContentsTheSame(a, b) } -> false

        else -> true
    }

}
