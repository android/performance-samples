package org.example

import android.util.Log
import androidx.annotation.keep.UsesReflectionToConstruct

// The library is given a direct reference to the app's task class.
interface StartupTask {
    fun run()
}

// Reflection with class loaded by name
// The library object that loads and executes the task.
object TaskRunner {
    @UsesReflectionToConstruct(
        classConstant = StartupTask::class,
        parameterTypes = []
    )
    fun execute(taskClass: Class<out StartupTask>) {
        // The class isn't removed, but its constructor might be.
        val task = taskClass.getDeclaredConstructor().newInstance()
        task.run()
    }
}

// The library object that loads and executes the task.
object TaskRunner2 {
    @UsesReflectionToConstruct(
        classConstant = StartupTask::class,
        parameterTypes = []
    )
    fun execute(className: String) {
        // R8 won't retain classes specified by this string value at runtime
        val taskClass = Class.forName(className)
        val task = taskClass.getDeclaredConstructor().newInstance() as StartupTask
        task.run()
    }
}

// The app's task is to pre-cache data.
class PreCacheTask : StartupTask {
    override fun run() {
        Log.e("AppTask", "Warming up the cache...")
    }
}
