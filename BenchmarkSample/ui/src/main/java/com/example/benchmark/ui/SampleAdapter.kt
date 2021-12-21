package com.example.benchmark.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class SampleAdapter : ListAdapter<String, Holder>(StringDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder.create(parent)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}

private object StringDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem
}
