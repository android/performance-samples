package com.example.benchmark

import android.view.View
import android.view.ViewGroup
import androidx.benchmark.BenchmarkRule
import androidx.benchmark.measureRepeated
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.benchmark.ui.MainActivity
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * RecyclerView benchmark - scrolls a RecyclerView, and measures the time taken to reveal each item
 *
 * You can use this general approach to benchmark the performance of RecyclerView. Some things to be aware of:
 *
 * - Benchmark one ItemView type at a time. If you have for example section headers, or other types of item variation,
 *      it's recommended to use fake adapter data with just a single type of item at a time.
 * - If you want to benchmark TextView performance, use randomized text. Reusing words between items (such as in this
 *      simple test) will artificially perform better due to unrealistic caching.
 * - You won't see the effects of RecyclerView prefetching, or Async text layout with this simple approach. We'll be
 *      adding more complex RecyclerView examples as time goes on.
 *
 * Note that this benchmark measures the sum of multiple potentially expensive stages of displaying an item:
 * - Attaching an ItemView to RecyclerView
 * - Detaching an ItemView (scrolling out of viewport) from RecyclerView
 * - onBindViewHolder
 * - ItemView layout
 *
 * It does *not* measure any of the following work:
 * - onCreateViewHolder
 * - RenderThread and GPU Rendering work
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class RecyclerViewBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        activityRule.scenario.onActivity {
            // Initialize the Adapter with fake data.
            // (Submit null first so both are synchronous for simplicity)
            // ItemViews will be inflated and ready by the next onActivity callback
            it.adapter.submitList(null)
            it.adapter.submitList(List(100000) { "List Item $it" })
        }
    }

    @After
    fun teardown() {
        activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
    }

    /**
     * Measure the cost of scrolling RecyclerView by one ViewHolder.
     */
    @Test
    fun simpleScroll() {
        activityRule.scenario.onActivity {
            // If RecyclerView has children, the items are attached, bound, and gone through layout. Ready to benchmark.
            assertTrue("RecyclerView expected to have children", it.recyclerView.childCount > 0)

            benchmarkRule.measureRepeated {
                // Scroll RecyclerView by one item
                // this will synchronously execute: attach / detach(old item) / bind / layout
                it.recyclerView.scrollBy(0, it.recyclerView.getLastChild().height)
            }
        }
    }
}

private fun ViewGroup.getLastChild(): View = getChildAt(childCount - 1)
