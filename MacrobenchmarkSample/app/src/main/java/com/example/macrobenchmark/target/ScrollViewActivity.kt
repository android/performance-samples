package com.example.macrobenchmark.target

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.macrobenchmark.target.databinding.ActivityScrollViewBinding

class ScrollViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "ScrollView Sample"
        val binding = ActivityScrollViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val itemCount = intent.getIntExtra(RecyclerViewActivity.EXTRA_ITEM_COUNT, 1000)

        val items = List(itemCount) {
            Entry("Item $it")
        }

        val parent = binding.scrollcontent
        val inflater = LayoutInflater.from(parent.context)

        items.forEach { entry ->
            val itemView = inflater.inflate(R.layout.recycler_row, parent, false)
            parent.addView(itemView)
            val contentView = itemView.findViewById<TextView>(R.id.content)
            contentView.text = entry.contents
            itemView.setOnClickListener {
                ClickTrace.onClickPerformed()
            }
        }
    }
}