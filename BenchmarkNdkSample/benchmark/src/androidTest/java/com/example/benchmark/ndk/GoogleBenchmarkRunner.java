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
package com.example.benchmark.ndk;

import android.Manifest;
import android.os.Build;

import androidx.benchmark.BenchmarkState;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.util.Collections;

/**
 * Custom Runner that implements a bridge between JUnit and Google Benchmark.
 *
 * Use this Runner in a @RunWith annotation together with a @TargetLibrary
 * annotation on an empty class to create a CTS test that consists of native
 * tests written against the Google Test Framework. See the CTS module in
 * cts/tests/tests/nativehardware for an example.
 */
public class GoogleBenchmarkRunner extends Runner {
    private static boolean sOnceFlag = false;

    private Class mTargetClass;
    private Description mDescription;
    public GoogleBenchmarkRunner(Class testClass) {
        synchronized (GoogleBenchmarkRunner.class) {
            if (sOnceFlag) {
                throw new IllegalStateException("Error, multiple GoogleBenchmarkRunners defined");
            }
            sOnceFlag = true;
        }
        mTargetClass = testClass;
        System.loadLibrary("ndkbenchmark");
        mDescription = Description.createSuiteDescription(testClass);
        // The nInitialize native method will populate the description based on
        // GTest test data.
        nInitialize(testClass.getName(), mDescription);

        if (Build.VERSION.SDK_INT >= 21) {
            // Force-grant WRITE_EXTERNAL_STORAGE, used by the Jetpack Benchmark library.
            // The library stores benchmark results in external storage, and these results are
            // copied by the gradle plugin to `benchmark/build/benchmark_reports/...`
            // (Since we can't use a typical GrantPermissionRule with this custom Runner)
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + ApplicationProvider.getApplicationContext().getPackageName()
                            + " " + Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public Description getDescription() {
        return mDescription;
    }

    @Override
    public void run(RunNotifier notifier) {
        nRun(mTargetClass.getName(), notifier);
    }

    @SuppressWarnings("unused") // called by native
    public static void reportRun(String benchmarkName, long nanos) {
        // Report the observed measurement to Jetpack Benchmark.
        // Note that because google/benchmark doesn't report how much warmup, or how many iterations
        // are done between each measurement, we don't report those correctly.
        BenchmarkState.reportData(
                "",
                benchmarkName,
                nanos,
                Collections.singletonList(nanos),
                /* warmupIterations */0,
                /* repeatIterations */1);
    }

    /**
     * Initialize description with a child for every runnable benchmark
     */
    private static native void nInitialize(String className, Description description);

    /**
     * Run all benchmarks
     */
    private static native boolean nRun(String className, RunNotifier notifier);
}
