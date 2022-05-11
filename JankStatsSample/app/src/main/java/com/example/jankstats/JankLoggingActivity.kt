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
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.PerformanceMetricsState
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.jankstats.databinding.ActivityJankLoggingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

/**
 * This activity shows the basic usage of JankStats, from creating and enabling it to track
 * a view hierarchy, to setting application state on JankStats, to receiving and logging per-frame
 * callbacks with jank data.
 */
// [START activity_init]
class JankLoggingActivity : AppCompatActivity() {

    private lateinit var jankStats: JankStats
    // [START_EXCLUDE silent]
    private lateinit var binding: ActivityJankLoggingBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    // [START jank_frame_listener]
    private val jankFrameListener = JankStats.OnFrameListener { frameData ->
        // A real app could do something more interesting, like writing the info to local storage and later on report it.
        Log.v("JankStatsSample", frameData.toString())
    }
    // [END jank_frame_listener]
    // [END_EXCLUDE]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START_EXCLUDE]
        binding = ActivityJankLoggingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUi()
        // [END_EXCLUDE]
        // metrics state holder can be retrieved regardless of JankStats initialization
        val metricsStateHolder = PerformanceMetricsState.getForHierarchy(binding.root)

        // initialize JankStats for current window
        jankStats = JankStats.createAndTrack(
            window,
            Dispatchers.Default.asExecutor(),
            jankFrameListener,
        )

        // add activity name as state
        metricsStateHolder.state?.addState("Activity", javaClass.simpleName)
        // [START_EXCLUDE]
        setupNavigationState()
        // [END_EXCLUDE]
    }
    // [END activity_init]

    // [START tracking_enabled]
    override fun onResume() {
        super.onResume()
        jankStats.isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
        jankStats.isTrackingEnabled = false
    }
    // [END tracking_enabled]

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupUi() {
        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_container) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupNavigationState() {
        // [START state_navigation]
        val metricsStateHolder = PerformanceMetricsState.getForHierarchy(binding.root)
        // add current navigation information into JankStats state
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            metricsStateHolder.state?.addState(
                "Navigation",
                "Args(${arguments.toString()}), $destination"
            )
        }
        // [END state_navigation]
    }
}
