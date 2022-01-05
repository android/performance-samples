package com.example.macrobenchmark.startup;

import static com.example.macrobenchmark.UtilsKt.TARGET_PACKAGE;

import androidx.benchmark.macro.StartupTimingMetric;
import androidx.benchmark.macro.junit4.MacrobenchmarkRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

import kotlin.Unit;

// [START macrobenchmark_startup_java]
@RunWith(AndroidJUnit4.class)
public class SampleStartupBenchmarkJava {
    @Rule
    public MacrobenchmarkRule benchmarkRule = new MacrobenchmarkRule();

    @Test
    public void startup() {
        benchmarkRule.measureRepeated(
                /* packageName */ TARGET_PACKAGE,
                /* metrics */ Collections.singletonList(new StartupTimingMetric()),
                /* iterations */ 3,
                scope -> {
                    scope.startActivityAndWait();
                    return Unit.INSTANCE;
                }
        );
    }
}
// [END macrobenchmark_startup_java]
