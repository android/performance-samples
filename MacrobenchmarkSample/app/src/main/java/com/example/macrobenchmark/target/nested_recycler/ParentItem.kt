package com.example.macrobenchmark.target.nested_recycler

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
