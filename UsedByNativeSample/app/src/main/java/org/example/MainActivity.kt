package org.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.keep.UsesReflectionToAccessMethod
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.jni.JniBridge
import org.example.jni.NativeLib
import org.example.ui.theme.ExampleTheme

class MainActivity : ComponentActivity() {
    // Data objects that C++ passed to Kotlin through the JNI upcalls, shown on screen.
    private val nativeResults = mutableStateListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                        Greeting(name = "Android")
                        NativeResults(results = nativeResults)
                    }
                }
            }
        }


        // Call Reflection Examples
        try {
            // JNI downcall (jni/DowncallExample.kt): Kotlin -> C++. The arguments go down to C++
            // and the results come back as return values.
            val nativeLib = NativeLib()
            val sum = nativeLib.add(2, 3)
            val greeting = nativeLib.greet("R8")
            Log.e("NativeLib", "Downcall results: add(2, 3) = $sum, greet(\"R8\") = $greeting")
            nativeResults += "Downcall: add(2, 3) = $sum"
            nativeResults += "Downcall: greet(\"R8\") = $greeting"

            // JNI upcall (jni/UpcallExample.kt): C++ -> Kotlin. The triggerUpcall() downcall makes
            // C++ construct a NativeData and pass it to JniBridge.onNativeEvent().
            JniBridge { data -> nativeResults += "Upcall: onNativeEvent($data)" }.triggerUpcall()



            // Test Serializable
            SerializableTester.testSerialization()

            Log.e("MainActivity", "All reflection examples executed successfully.")
        } catch (e: Throwable) {
            // JNI lookup failures are Errors (NoSuchMethodError, NoClassDefFoundError,
            // UnsatisfiedLinkError), so catch Throwable to log them before crashing.
            Log.e("MainActivity", "Reflection example failed", e)
            throw RuntimeException("Reflection example failed", e)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

// Shows the results of the JNI downcall and upcall examples.
@Composable
fun NativeResults(results: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(top = 16.dp)) {
        Text(text = "JNI examples (org.example.jni):")
        results.forEach { Text(text = it, modifier = Modifier.padding(top = 8.dp)) }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExampleTheme {
        Greeting("Android")
    }
}