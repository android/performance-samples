package com.example.benchmark.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

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

// stable random for having the same results
private val random = Random(0)

fun doSomeWork() {
    // Pretend this method does some work
    Thread.sleep(random.nextLong(1_000))
}
