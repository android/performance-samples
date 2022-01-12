package com.example.benchmark.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    // It's used in benchmarks
    @Suppress("MemberVisibilityCanBePrivate")
    val adapter = SampleAdapter()

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.adapter = adapter
    }
}
