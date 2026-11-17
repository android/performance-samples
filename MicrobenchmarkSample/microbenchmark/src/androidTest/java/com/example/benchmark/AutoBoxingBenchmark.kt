/*
 * Copyright 2019 The Android Open Source Project
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

package com.example.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AutoBoxingBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    /**
     * Measure the cost of allocating a boxed integer that takes advantage of ART's cache.
     */
    @Test
    fun integerArtCacheAlloc() {
        var i: Int? = 0
        benchmarkRule.measureRepeated {
            i = next(i)
        }
    }

    /**
     * Measure the cost of allocating a boxed integer that falls outside the range of ART's cache.
     */
    @Test
    fun integerAlloc() {
        var i: Int? = 1000
        benchmarkRule.measureRepeated {
            i = nextBig(i)
        }
    }

    /**
     * Keep this function and [nextBig] as regular (non-inline) functions and do not move their
     * bodies into the [BenchmarkRule.measureRepeated] block.
     * The block is compiled into a suspend lambda, and in coroutine code Kotlin boxes primitives
     * via boxing.kt functions like [kotlin.coroutines.jvm.internal.boxInt],
     * which call the [Integer] constructor instead of [Integer.valueOf]. Boxing done inside
     * the block would therefore allocate on every iteration and never use the Integer cache.
     */
    private fun next(i: Int?): Int? = if (i!! < 100) i + 1 else 0

    private fun nextBig(i: Int?): Int? = if (i!! < 1100) i + 1 else 1000
}
