package org.example

import android.util.Log
import androidx.annotation.keep.UnconditionallyKeep
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
            Log.e(TAG, "Video tracking enabled.")
        } catch (e: ClassNotFoundException) {
            Log.e(TAG,"Video tracking module not found. Skipping.")
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
        }
    }
}