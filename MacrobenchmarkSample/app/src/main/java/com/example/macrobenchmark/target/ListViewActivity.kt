package com.example.macrobenchmark.target

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.macrobenchmark.target.databinding.ActivityListViewBinding

class ListViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "ListView Sample"
        val binding = ActivityListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val itemCount = intent.getIntExtra(RecyclerViewActivity.EXTRA_ITEM_COUNT, 1000)

        val items = List(itemCount) {
            Entry("Item $it")
        }

        binding.listview.adapter = object : BaseAdapter() {
            override fun getCount() = itemCount

            override fun getItem(position: Int) = items[position]

            override fun getItemId(position: Int) = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val itemView = if (convertView == null) {
                    val inflater = LayoutInflater.from(parent.context)
                    inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                } else {
                    convertView
                }

                val contentView = itemView.findViewById<TextView>(android.R.id.text1)

                contentView.text = getItem(position).contents
                return contentView
            }
        }

        binding.listview.setOnItemClickListener { _, _, _, _ ->
            ClickTrace.onClickPerformed()
        }

    }
}