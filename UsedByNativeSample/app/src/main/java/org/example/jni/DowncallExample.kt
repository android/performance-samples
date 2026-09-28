package org.example.jni

/**
 * Downcall: Kotlin calls into C++ (native-lib.cpp).
 *
 * No custom keep rule is needed. The default proguard-android-optimize.txt already contains
 *
 *     -keepclasseswithmembernames,includedescriptorclasses class * {
 *         native <methods>;
 *     }
 *
 * which stops R8 from renaming native methods and their classes. The C++ symbols
 * Java_org_example_jni_NativeLib_add and Java_org_example_jni_NativeLib_greet encode these names,
 * so if R8 renamed them, the calls would fail with UnsatisfiedLinkError.
 */
class NativeLib {
    // Arguments are passed down to C++ and the result is returned to Kotlin.
    external fun add(a: Int, b: Int): Int

    external fun greet(name: String): String

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}
