package com.example.macrobenchmark.target

import android.app.Application

class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ClickTrace.install()
    }
}