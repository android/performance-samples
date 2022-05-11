/*
 * Copyright 2021 The Android Open Source Project
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

import android.view.LayoutInflater
import android.view.View.MeasureSpec
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.benchmark.ui.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.OrderWith
import org.junit.runner.RunWith
import org.junit.runner.manipulation.Alphanumeric

@LargeTest
@RunWith(AndroidJUnit4::class)
@OrderWith(Alphanumeric::class)
class ViewMeasureLayoutBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun constraintLayoutHierarchy_AT_MOST() {
        benchmarkMeasureLayout(R.layout.activity_constraintlayout, MeasureSpec.AT_MOST)
    }

    @Test
    fun constraintLayoutHierarchy_EXACTLY() {
        benchmarkMeasureLayout(R.layout.activity_constraintlayout, MeasureSpec.EXACTLY)
    }

    @Test
    fun traditionalViewHierarchy_AT_MOST() {
        benchmarkMeasureLayout(R.layout.activity_traditional, MeasureSpec.AT_MOST)
    }

    @Test
    fun traditionalViewHierarchy_EXACTLY() {
        benchmarkMeasureLayout(R.layout.activity_traditional, MeasureSpec.EXACTLY)
    }

    private fun benchmarkMeasureLayout(layoutRes: Int, mode: Int) {
        val context = InstrumentationRegistry.getInstrumentation().context
        val inflater = LayoutInflater.from(context)

        benchmarkRule.measureRepeated {
            // Not to use the view cache in the View class, we inflate it every time
            val container = runWithTimingDisabled { inflater.inflate(layoutRes, null) }

            val widthMeasureSpec = MeasureSpec.makeMeasureSpec(1080, mode)
            val heightMeasureSpec = MeasureSpec.makeMeasureSpec(1920, mode)

            container.measure(widthMeasureSpec, heightMeasureSpec)
            container.layout(0, 0, container.measuredWidth, container.measuredHeight)
        }
    }
}
