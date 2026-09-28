package org.example

import android.util.Log

// The library is given a direct reference to the app's task class.
interface StartupTask {
    fun run()
}

// The library object that loads and executes the task.
object TaskRunner {
    fun execute(taskClass: Class<out StartupTask>) {
        // The class isn't removed, but its constructor might be.
        val task = taskClass.getDeclaredConstructor().newInstance()
        task.run()
    }
}

// The app's task is to pre-cache data.
class PreCacheTask : StartupTask {
    override fun run() {
        Log.d("AppTask", "Warming up the cache...")
    }
}
