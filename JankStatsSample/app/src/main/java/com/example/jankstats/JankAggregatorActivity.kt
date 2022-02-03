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

package com.example.jankstats

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.metrics.performance.PerformanceMetricsState
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.jankstats.databinding.ActivityJankLoggingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

/**
 * This activity shows how to use JankStatsAggregator, a class in this test directory layered
 * on top of JankStats which aggregates the per-frame data. Instead of receiving jank data
 * per frame (which would happen by using JankStats directly), the report listener only
 * receives data when a report is issued, either when the activity goes into the background
 * or if JankStatsAggregator issues the report itself.
 */
// [START aggregator_activity_init]
class JankAggregatorActivity : AppCompatActivity() {

    private lateinit var jankStatsAggregator: JankStatsAggregator
    // [START_EXCLUDE silent]
    private lateinit var binding: ActivityJankLoggingBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    // [START jank_aggregator_listener]
    private val jankReportListener =
        JankStatsAggregator.OnJankReportListener { reason, totalFrames, jankFrameData ->
            // A real app could do something more interesting, like writing the info to local storage and later on report it.

            Log.v(
                "JankStatsSample",
                "*** Jank Report ($reason), " +
                        "totalFrames = $totalFrames, " +
                        "jankFrames = ${jankFrameData.size}"
            )

            jankFrameData.forEach { frameData ->
                Log.v("JankStatsSample", frameData.toString())
            }
        }
    // [END jank_aggregator_listener]
    // [END_EXCLUDE silent]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START_EXCLUDE]
        binding = ActivityJankLoggingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUi()
        // [END_EXCLUDE]
        // Metrics state holder can be retrieved regardless of JankStats initialization.
        val metricsStateHolder = PerformanceMetricsState.getForHierarchy(binding.root)

        // Initialize JankStats with an aggregator for the current window.
        jankStatsAggregator = JankStatsAggregator(
            window,
            Dispatchers.Default.asExecutor(),
            jankReportListener
        )

        // Add the Activity name as state.
        metricsStateHolder.state?.addState("Activity", javaClass.simpleName)
    }
    // [END aggregator_activity_init]

    // [START aggregator_tracking_enabled]
    override fun onResume() {
        super.onResume()
        jankStatsAggregator.jankStats.isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
        // Before disabling tracking, issue the report with (optionally) specified reason.
        jankStatsAggregator.issueJankReport("Activity paused")
        jankStatsAggregator.jankStats.isTrackingEnabled = false
    }
    // [END aggregator_tracking_enabled]

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupUi() {
        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_container) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}
