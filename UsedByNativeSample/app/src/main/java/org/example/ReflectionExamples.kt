package org.example

import android.util.Log
import androidx.annotation.keep.UsesReflectionToAccessField
import androidx.annotation.keep.UsesReflectionToConstruct

// --- Example: Optional feature module loaded via reflection ---

object AnalyticsManager {
    private const val TAG = "AnalyticsManager"
    private const val VIDEO_TRACKER_CLASS = "org.example.VideoEventTracker"

    @UsesReflectionToConstruct(
        className = "org.example.VideoEventTracker",
        parameterTypes = []
    )
    fun initialize() {
        try {
            // Attempt to load the optional module's class using reflection
            Class.forName(VIDEO_TRACKER_CLASS).getDeclaredConstructor().newInstance()
            Log.d(TAG, "Video tracking enabled.")
        } catch (e: ClassNotFoundException) {
            Log.d(TAG,"Video tracking module not found. Skipping.")
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
        }
    }
}

// In a real app, this might be in a different module
class VideoEventTracker {
    // This constructor must be kept for the reflection call to succeed.
    init { /* ... */ }
}

// --- Example: Reflection to access private members ---

class LibraryClass {
    // This field might be removed by R8 if not kept
    private val secretMessage: String = "Hidden secret"
}

fun accessSecretMessage(instance: LibraryClass) {
    // Use Java reflection from Kotlin to access the private field
    val secretField = instance::class.java.getDeclaredField("secretMessage")
    secretField.isAccessible = true
    // This will crash at runtime with R8 enabled if the field is not kept
    val message = secretField.get(instance) as String
    Log.d("ReflectionExample", "Secret message: $message")
}
