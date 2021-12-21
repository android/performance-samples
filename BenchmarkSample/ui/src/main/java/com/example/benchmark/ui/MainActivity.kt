package com.example.benchmark.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random.Default.nextLong

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

fun doSomeWork() {
    // Pretend this method does some work
    Thread.sleep(nextLong(1_000))
}
