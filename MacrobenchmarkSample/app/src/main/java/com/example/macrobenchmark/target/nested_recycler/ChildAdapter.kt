package com.example.macrobenchmark.target.nested_recycler

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.tracing.trace
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
