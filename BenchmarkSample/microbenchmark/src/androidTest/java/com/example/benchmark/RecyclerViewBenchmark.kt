package com.example.benchmark

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.benchmark.ui.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * RecyclerView benchmark - scrolls a RecyclerView, and measures the time taken to reveal each item
 *
 * You can use this general approach to benchmark the performance of RecyclerView. Some things to be
 * aware of:
 *
 * - Benchmark one ItemView type at a time. If you have for example section headers, or other types
 *      of item variation, it's recommended to use fake adapter data with just a single type of item
 *      at a time.
 * - If you want to benchmark TextView performance, use randomized text. Reusing words between items
 *      (such as in this simple test) will artificially perform better than real world usage, due to
 *      unrealistic layout cache hit rates.
 * - You won't see the effects of RecyclerView prefetching, or Async text layout with this simple
 *      approach. We'll add more complex RecyclerView examples as time goes on.
 *
 * This benchmark measures the sum of multiple potentially expensive stages of displaying an item:
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
    
    class LazyComputedList<T>(
        override val size: Int = Int.MAX_VALUE,
        private inline val compute: (Int) -> T
    ) : AbstractList<T>() {
        override fun get(index: Int): T = compute(index)
    }

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        activityRule.scenario.onActivity { activity ->
            // Set the RecyclerView to have a height of 1 pixel.
            // This ensures that only one item can be displayed at once.
            activity.recyclerView.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, 1)

            // Initialize the Adapter with fake data.
            // (Submit null first so both are synchronous for simplicity)
            // 1st ViewHolder will be inflated and displayed by the next onActivity callback
            activity.adapter.submitList(null)
            activity.adapter.submitList(LazyComputedList { buildRandomParagraph() })
        }
    }

    @Test
    fun buildParagraph() {
        benchmarkRule.measureRepeated {
            // measure cost of generating paragraph - this is overhead in the primary scroll()
            // benchmark, but is a very small fraction of the amount of work there.
            buildRandomParagraph()
        }
    }

    @UiThreadTest
    @Test
    fun scroll() {
        activityRule.scenario.onActivity { activity ->
            val recyclerView = activity.recyclerView

            assertTrue("RecyclerView expected to have children", recyclerView.childCount > 0)
            assertEquals("RecyclerView must have height = 1", 1, recyclerView.height)

            // RecyclerView has children, its items are attached, bound, and have gone through layout.
            // Ready to benchmark!
            benchmarkRule.measureRepeated {
                // Scroll RecyclerView by one item
                // this will synchronously execute: attach / detach(old item) / bind / layout
                recyclerView.scrollBy(0, recyclerView.getLastChild().height)
            }
        }
    }
}

private fun ViewGroup.getLastChild(): View = getChildAt(childCount - 1)
