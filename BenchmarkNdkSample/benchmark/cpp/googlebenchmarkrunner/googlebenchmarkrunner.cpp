/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include <unordered_map>
#include <vector>
#include <jni.h>
#include <benchmark/benchmark.h>
#include <ScopedLocalRef.h>
#include <sstream>
#include <iostream>
#include <vector>


namespace {
    struct {
        jclass clazz;
        /** static methods **/
        jmethodID createTestDescription;
        /** methods **/
        jmethodID addChild;
    } gDescription;
    struct {
        jclass clazz;
        jmethodID fireTestStarted;
        jmethodID fireTestFinished;
        jmethodID fireTestFailure;
    } gRunNotifier;
    struct {
        jclass clazz;
        jmethodID ctor;
    } gAssertionFailure;
    struct {
        jclass clazz;
        jmethodID ctor;
    } gFailure;
    struct {
        jclass clazz;
        /** static methods **/
        jmethodID reportRun;
    } gGoogleBenchmarkRunner;
    jobject gEmptyAnnotationsArray;

    // Return the full native test name as a Java method name, which does not allow slashes or dots.
    std::string mangleTestName(const std::string &nativeName) {
        std::string mangledName = nativeName;
        std::replace(mangledName.begin(), mangledName.end(), '.', '_');
        std::replace(mangledName.begin(), mangledName.end(), '/', '_');
        return mangledName;
    }

    // Creates org.junit.runner.Description object for a GoogleBenchmark given its name.
    jobject createTestDescription(JNIEnv* env, jstring className, const std::string& mangledName) {
        ScopedLocalRef<jstring> jTestName(env, env->NewStringUTF(mangledName.c_str()));
        return env->CallStaticObjectMethod(gDescription.clazz, gDescription.createTestDescription,
                                           className, jTestName.get(), gEmptyAnnotationsArray);
    }

    jobject createTestDescription(JNIEnv* env, jstring className, const char* testName) {
        std::string mangledName = mangleTestName(std::string(testName));
        return createTestDescription(env, className, mangledName);
    }

    void addChild(JNIEnv* env, jobject description, jobject childDescription) {
        env->CallVoidMethod(description, gDescription.addChild, childDescription);
    }
}  // namespace

class NoopReporter : public ::benchmark::BenchmarkReporter {
public:
    NoopReporter() = default;
    bool ReportContext(const Context &context) override { return true; }
    void ReportRuns(const std::vector<Run> &reports) override {}
    void Finalize() override {}
};

void getBenchmarks(std::vector<std::string>* output) {
    int argc = 2;
    const char* argv[] = { "ignored", "--benchmark_list_tests=true" };
    ::benchmark::Initialize(&argc, (char**) argv);

    // capture list of benchmarks in outStream
    NoopReporter reporter;
    std::ostringstream outStream;
    reporter.SetOutputStream(&outStream);
    ::benchmark::RunSpecifiedBenchmarks(&reporter);

    // convert to vector<string>
    std::istringstream stream(outStream.str());
    std::string currentBenchmarkName;
    while (std::getline(stream, currentBenchmarkName)) {
        output->push_back(currentBenchmarkName);
    }
}

/**
 * Report Benchmarks up through JNI
 *
 * start & stop to JUnit via the GoogleBenchmarkRunner
 * result in ns to Android Benchmark library
 */
class JUnitReporter : public ::benchmark::BenchmarkReporter {
public:
    JUnitReporter(JNIEnv *env, jstring className, jobject runNotifier)
            : mEnv(env)
            , mRunNotifier(runNotifier)
            , mClassName(className)
            , mCurrentTestDescription { env, nullptr } {
        getBenchmarks(&testNames);
    }

    ~JUnitReporter() override = default;

    bool ReportContext(const Context &context) override {
        notifyTestStarted();
        return true;
    }

    void ReportRuns(const std::vector<Run> &reports) override {
        for (auto &run : reports) {
            if (run.error_occurred) {
                ScopedLocalRef<jstring> jmessage(mEnv, mEnv->NewStringUTF(run.error_message.c_str()));
                ScopedLocalRef<jobject> jthrowable(mEnv, mEnv->NewObject(
                        gAssertionFailure.clazz, gAssertionFailure.ctor, jmessage.get()));
                ScopedLocalRef<jobject> jfailure(mEnv, mEnv->NewObject(
                        gFailure.clazz, gFailure.ctor, mCurrentTestDescription.get(), jthrowable.get()));
                mEnv->CallVoidMethod(mRunNotifier, gRunNotifier.fireTestFailure, jfailure.get());
            } else {
                ScopedLocalRef<jstring> jTestName(mEnv, mEnv->NewStringUTF(run.benchmark_name().data()));
                double resultNs = run.GetAdjustedRealTime() * (1e9 / ::benchmark::GetTimeUnitMultiplier(run.time_unit));
                mEnv->CallStaticVoidMethod(
                        gGoogleBenchmarkRunner.clazz,
                        gGoogleBenchmarkRunner.reportRun,
                        jTestName.get(),
                        (int64_t) resultNs);
            }

            // always fire finished, even if error occurred
            notify(gRunNotifier.fireTestFinished);

            // notify next test starting, if one exists
            notifyTestStarted();
        }
    }

    void Finalize() override {}

private:
    void notify(jmethodID method) {
        mEnv->CallVoidMethod(mRunNotifier, method, mCurrentTestDescription.get());
    }

    /**
     * Because google benchmark doesn't tell us when a test starts, we use a known list of tests,
     * and trigger fireTestStarted when we know a test is about to start.
     */
    void notifyTestStarted() {
        if (!testNames.empty()) {
            mCurrentTestDescription.reset(
                    createTestDescription(mEnv, mClassName, testNames[0].data()));
            notify(gRunNotifier.fireTestStarted);

            testNames.erase(testNames.begin());
        }
    }

    JNIEnv* mEnv;
    jobject mRunNotifier;
    jstring mClassName;

    std::vector<std::string> testNames;
    ScopedLocalRef<jobject> mCurrentTestDescription;
};

extern "C"
JNIEXPORT void JNICALL
Java_com_example_benchmark_ndk_GoogleBenchmarkRunner_nInitialize(
        JNIEnv *env, jclass, jstring className, jobject suite) {
    // Stash JNI entries for faster lookup

    // Description
    gDescription.clazz = (jclass) env->NewGlobalRef(env->FindClass("org/junit/runner/Description"));
    gDescription.createTestDescription = env->GetStaticMethodID(
            gDescription.clazz, "createTestDescription",
            "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/annotation/Annotation;)Lorg/junit/runner/Description;");
    gDescription.addChild = env->GetMethodID(
            gDescription.clazz, "addChild", "(Lorg/junit/runner/Description;)V");

    // Annotation
    jclass annotations = env->FindClass("java/lang/annotation/Annotation");
    gEmptyAnnotationsArray = env->NewGlobalRef(env->NewObjectArray(0, annotations, nullptr));

    // AssertionFailure
    gAssertionFailure.clazz = (jclass) env->NewGlobalRef(env->FindClass("java/lang/AssertionError"));
    gAssertionFailure.ctor = env->GetMethodID(gAssertionFailure.clazz, "<init>", "(Ljava/lang/Object;)V");

    // Failure
    gFailure.clazz = (jclass) env->NewGlobalRef(env->FindClass("org/junit/runner/notification/Failure"));
    gFailure.ctor = env->GetMethodID(
            gFailure.clazz, "<init>", "(Lorg/junit/runner/Description;Ljava/lang/Throwable;)V");

    // RunNotifier
    gRunNotifier.clazz = (jclass) env->NewGlobalRef(
            env->FindClass("org/junit/runner/notification/RunNotifier"));
    gRunNotifier.fireTestStarted = env->GetMethodID(
            gRunNotifier.clazz, "fireTestStarted", "(Lorg/junit/runner/Description;)V");
    gRunNotifier.fireTestFinished = env->GetMethodID(
            gRunNotifier.clazz, "fireTestFinished", "(Lorg/junit/runner/Description;)V");
    gRunNotifier.fireTestFailure = env->GetMethodID(
            gRunNotifier.clazz, "fireTestFailure", "(Lorg/junit/runner/notification/Failure;)V");

    // GoogleBenchmarkRunner
    gGoogleBenchmarkRunner.clazz = (jclass) env->NewGlobalRef(
            env->FindClass("com/example/benchmark/ndk/GoogleBenchmarkRunner"));
    gGoogleBenchmarkRunner.reportRun = env->GetStaticMethodID(
            gGoogleBenchmarkRunner.clazz, "reportRun", "(Ljava/lang/String;J)V");


    // register each found benchmark as a child test description
    std::vector<std::string> testNames;
    getBenchmarks(&testNames);
    for (auto& testName : testNames) {
        ScopedLocalRef<jobject> testDescription(env, createTestDescription(env, className, testName));
        addChild(env, suite, testDescription.get());
    }
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_benchmark_ndk_GoogleBenchmarkRunner_nRun(JNIEnv *env, jclass, jstring className, jobject notifier) {
    JUnitReporter reporter{env, className, notifier};
    // note - have to disable '--benchmark_list_tests' explicitly, since it controls a sticky global flag
    int argc = 2;
    const char* argv[] = { "ignored", "--benchmark_list_tests=false" };
    ::benchmark::Initialize(&argc, (char**) argv);
    ::benchmark::RunSpecifiedBenchmarks(&reporter);
    return (jboolean) false;
}
