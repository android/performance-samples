package org.example.jni

import android.util.Log
import com.google.android.apps.common.proguard.UsedByNative

/**
 * Upcall: C++ (native-lib.cpp) calls back into Kotlin.
 *
 * R8 does not analyze C++, so everything C++ looks up by name is invisible to it: the NativeData
 * class, its constructor and JniBridge.onNativeEvent(). They are kept by @UsedByNative together
 * with the reusable @UsedByNative rules in rules.keep.
 *
 * C++ finds NativeData by name (FindClass) and constructs it (NewObject), so the class itself must
 * be kept. The `-keepclasseswithmembers class * { @UsedByNative <init>(...); }` rule keeps (and
 * doesn't rename) every class that has an @UsedByNative constructor, plus that constructor, so the
 * class needs no annotation of its own. The constructor must be annotated explicitly: in Kotlin,
 * `@UsedByNative val id` annotates the backing field, not the constructor.
 */
data class NativeData @UsedByNative constructor(
    @UsedByNative val id: Int,
    @UsedByNative val payload: String
)

// onEvent receives the data passed up from C++ (MainActivity uses it to show the data on screen).
class JniBridge(private val onEvent: (NativeData) -> Unit = {}) {
    @UsedByNative("Invoked from native C++ code via JNI upcall")
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
