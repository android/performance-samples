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

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.tracing.trace
import com.example.macrobenchmark.target.util.ClickTrace
import com.example.macrobenchmark.target.R
import com.example.macrobenchmark.target.databinding.ItemChildBinding

class ChildAdapter :
    ListAdapter<ChildItem, BindingViewHolder<ItemChildBinding>>(ChildItemDiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemChildBinding> {
        return trace("ChildAdapter.onCreateViewHolder") {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemChildBinding.inflate(inflater, parent, false)
            binding.text.setOnClickListener {
                ClickTrace.onClickPerformed()
            }
            BindingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemChildBinding>, position: Int) {
        trace("ChildAdapter.onBindViewHolder") {
            val item = getItem(position)

            holder.binding.avatar.setImageResource(R.drawable.ic_launcher_foreground)
            holder.binding.title.text = item.title
            holder.binding.subtitle.text = item.subtitle
            holder.binding.image.setImageBitmap(colorBitmap(item.color))
            holder.binding.text.text = item.description

            val highlightedTint = ColorStateList.valueOf(item.color)

            val normalTint = ColorStateList.valueOf(
                ContextCompat.getColor(holder.binding.root.context, R.color.black)
            )

            holder.binding.wishlist.imageTintList =
                if (item.inWishlist) highlightedTint else normalTint
            holder.binding.like.imageTintList =
                if (item.likeState == LikeState.LIKED) highlightedTint else normalTint
            holder.binding.dislike.imageTintList =
                if (item.likeState == LikeState.DISLIKED) highlightedTint else normalTint
        }
    }

    /**
     * Simulate loading an image
     */
    private fun colorBitmap(color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(600, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(color)

        return bitmap
    }

}
