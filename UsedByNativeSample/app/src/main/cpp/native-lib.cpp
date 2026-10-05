#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "NativeLib"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// =============================================================================================
// Downcalls (jni/DowncallExample.kt): Kotlin -> C++
// =============================================================================================
// The JNI symbol names encode the Kotlin package, class and method names
// (org.example.jni.NativeLib.add). R8 keeps those names because of the default
// `native <methods>` rule in proguard-android-optimize.txt, so no custom keep rule is needed.

extern "C" JNIEXPORT jint JNICALL
Java_org_example_jni_NativeLib_add(JNIEnv* /* env */, jobject /* this */, jint a, jint b) {
    LOGE("NativeLib.add(%d, %d) called from Kotlin", a, b);
    return a + b;
}

extern "C" JNIEXPORT jstring JNICALL
Java_org_example_jni_NativeLib_greet(JNIEnv* env, jobject /* this */, jstring name) {
    const char* chars = env->GetStringUTFChars(name, nullptr);
    if (chars == nullptr) return nullptr;  // OutOfMemoryError pending
    LOGE("NativeLib.greet(\"%s\") called from Kotlin", chars);
    std::string greeting = std::string("Hello ") + chars + " from C++";
    env->ReleaseStringUTFChars(name, chars);
    return env->NewStringUTF(greeting.c_str());
}

// =============================================================================================
// Upcall (jni/UpcallExample.kt): C++ -> Kotlin
// =============================================================================================
// R8 does not analyze C/C++ code, so every class, constructor and method that is looked up by
// name below is invisible to it. Each of them must be protected by a keep rule (here via
// explicit rules in rules.keep), otherwise the lookup fails after R8 runs. When a lookup fails, JNI returns
// null and leaves a Java exception pending (NoClassDefFoundError / NoSuchMethodError); we
// return early so that exception is rethrown in Kotlin when the native method returns.
static void triggerUpcall(JNIEnv* env, jobject bridgeObj) {
    // Find data model class and constructor
    jclass dataClass = env->FindClass("org/example/jni/NativeData");
    if (dataClass == nullptr) return;
    jmethodID dataInit = env->GetMethodID(dataClass, "<init>", "(ILjava/lang/String;)V");
    if (dataInit == nullptr) return;

    // Instantiate NativeData
    jstring payload = env->NewStringUTF("Event triggered from C++");
    jobject dataObj = env->NewObject(dataClass, dataInit, 42, payload);
    if (dataObj == nullptr) return;

    // Find callback method on JniBridge and invoke upcall
    jclass bridgeClass = env->GetObjectClass(bridgeObj);
    jmethodID onEventMethod = env->GetMethodID(bridgeClass, "onNativeEvent", "(Lorg/example/jni/NativeData;)V");
    if (onEventMethod == nullptr) return;
    env->CallVoidMethod(bridgeObj, onEventMethod, dataObj);
}

// Downcall entry point for JniBridge.triggerUpcall(), which starts the upcall above.
extern "C" JNIEXPORT void JNICALL
Java_org_example_jni_JniBridge_triggerUpcall(JNIEnv* env, jobject thiz) {
    LOGE("JniBridge.triggerUpcall(): calling back into Kotlin");
    triggerUpcall(env, thiz);
}
