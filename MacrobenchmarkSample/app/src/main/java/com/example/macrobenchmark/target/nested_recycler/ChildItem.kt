package com.example.macrobenchmark.target.nested_recycler

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
