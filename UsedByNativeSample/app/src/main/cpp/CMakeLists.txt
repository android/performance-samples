# Builds libnative-lib.so, which contains the JNI downcall entry points and the
# C++ -> Kotlin upcalls used by the R8 JNI keep rule examples.
cmake_minimum_required(VERSION 3.22.1)

project("r8examples")

add_library(native-lib SHARED native-lib.cpp)

# liblog provides __android_log_print, used to log from C++.
target_link_libraries(native-lib log)
