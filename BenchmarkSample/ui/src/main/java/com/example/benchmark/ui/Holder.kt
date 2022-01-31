package com.example.benchmark.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Holder(root: View) : RecyclerView.ViewHolder(root) {
    private val textView: TextView = root.findViewById(R.id.label)

    fun bind(string: String) {
        textView.text = string
    }

    companion object {
        fun create(parent: ViewGroup): Holder {
            val inflater = LayoutInflater.from(parent.context)
            return Holder(inflater.inflate(R.layout.item_view, parent, false))
        }
    }
}