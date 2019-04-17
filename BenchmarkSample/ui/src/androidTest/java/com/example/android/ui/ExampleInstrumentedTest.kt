package com.example.android.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertEquals("com.example.android.ui.test", appContext.packageName)
    }

    @Test
    fun resources() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val d = context.getDrawable(androidx.appcompat.R.drawable.abc_vector_test)
        assertFalse(d == null)
        assertEquals("android.graphics.drawable.VectorDrawable", d.javaClass.name)
    }
}
