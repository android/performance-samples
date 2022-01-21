package com.example.macrobenchmark

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import java.util.concurrent.TimeUnit

const val TARGET_PACKAGE = "com.example.macrobenchmark.target"


/**
 * Waits until an [android.app.Activity] with the given `className` is visible.
 */
fun UiDevice.waitUntilActivity(className: String) {
    wait(
        Until.hasObject(By.clazz(className)),
        TimeUnit.SECONDS.toMillis(10)
    )
}