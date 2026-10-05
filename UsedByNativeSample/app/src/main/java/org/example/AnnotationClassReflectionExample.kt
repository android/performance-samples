package org.example

import android.util.Log

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ReflectiveExecutor

class ReflectiveTaskRunner {
    fun process(task: Any) {
        val taskClass = task::class.java
        if (taskClass.isAnnotationPresent(ReflectiveExecutor::class.java)) {
            val methodToCall = taskClass.getMethod("execute")
            methodToCall.invoke(task)
        }
    }
}

@ReflectiveExecutor
class ImportantBackgroundTask {
    fun execute() {
        // This class will be removed by R8 without a keep rule
        Log.e("ImportantBackgroundTask", "Executing the important background task...")
    }
}
