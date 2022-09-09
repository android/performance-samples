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

data class ChildItem(
    val id: String,
    val parentId: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val color: Int,
    val inWishlist: Boolean = false,
    val likeState: LikeState = LikeState.UNKNOWN,
)

enum class LikeState {
    LIKED,
    DISLIKED,
    UNKNOWN
}

object ChildItemDiffCallback : DiffUtil.ItemCallback<ChildItem>() {
    override fun areItemsTheSame(oldItem: ChildItem, newItem: ChildItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ChildItem, newItem: ChildItem): Boolean = when {
        oldItem.color != newItem.color -> false
        oldItem.title != newItem.title -> false
        oldItem.subtitle != newItem.subtitle -> false
        oldItem.description != newItem.description -> false
        oldItem.inWishlist != newItem.inWishlist -> false
        oldItem.likeState != newItem.likeState -> false
        else -> true
    }

}
