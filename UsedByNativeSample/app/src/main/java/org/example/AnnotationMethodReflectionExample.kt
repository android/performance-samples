package org.example

import android.util.Log
import androidx.annotation.keep.UnconditionallyKeep
import androidx.annotation.keep.UsesReflectionToAccessMethod

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnEvent

class EventBus {
    @UsesReflectionToAccessMethod(
        classConstant = MyEventListener::class,
        methodName = "onSomethingHappened"
    )
    fun dispatch(listener: Any) {
        // Find all methods annotated with @OnEvent and invoke them
        listener::class.java.declaredMethods.forEach { method ->
            if (method.isAnnotationPresent(OnEvent::class.java)) {
                try {
                    method.invoke(listener)
                } catch (e: Exception) {

                }
            }
        }
    }
}

class MyEventListener {
    @OnEvent
    fun onSomethingHappened() {
        // This method will be removed by R8 without a keep rule
        Log.e("MyEventListener", "Event received method reflection")
    }
}
