package com.example.benchmark.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    val adapter = SampleAdapter()
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.adapter = adapter
    }
}

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
