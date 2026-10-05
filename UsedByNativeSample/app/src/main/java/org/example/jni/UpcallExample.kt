package org.example.jni

import android.util.Log

/**
 * Upcall: C++ (native-lib.cpp) calls back into Kotlin.
 *
 * R8 does not analyze C++, so everything C++ looks up by name is invisible to it: the NativeData
 * class, its constructor and JniBridge.onNativeEvent(). They are kept using explicit keep rules
 * in rules.keep without requiring annotations.
 *
 * C++ finds NativeData by name (FindClass) and constructs it (NewObject), so the class itself must
 * be kept along with the specific constructor. The rule `-keep class org.example.jni.NativeData { <init>(int, java.lang.String); }`
 * ensures R8 doesn't remove or rename the class and its constructor.
 */
data class NativeData(
    val id: Int,
    val payload: String
)

// onEvent receives the data passed up from C++ (MainActivity uses it to show the data on screen).
class JniBridge(private val onEvent: (NativeData) -> Unit = {}) {
    fun onNativeEvent(data: NativeData) {
        Log.e("JniBridge", "Received event from native code: $data")
        onEvent(data)
    }

    // Downcall into C++, which creates a NativeData and calls onNativeEvent() back (upcall).
    external fun triggerUpcall()

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}
