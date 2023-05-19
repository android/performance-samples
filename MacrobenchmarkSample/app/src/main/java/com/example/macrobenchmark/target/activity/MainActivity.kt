/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.macrobenchmark.target.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.profileinstaller.ProfileVerifier
import com.example.macrobenchmark.target.activity.clicklatency.ComposeActivity
import com.example.macrobenchmark.target.activity.clicklatency.ListViewActivity
import com.example.macrobenchmark.target.activity.clicklatency.NestedRecyclerActivity
import com.example.macrobenchmark.target.activity.clicklatency.NonExportedRecyclerActivity
import com.example.macrobenchmark.target.activity.clicklatency.ScrollViewActivity
import com.example.macrobenchmark.target.activity.clicklatency.USE_RECYCLER_POOLS
import com.example.macrobenchmark.target.activity.login.LoginActivity
import com.example.macrobenchmark.target.util.ClickTrace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                }
            ) {
                TopAppBar(title = { Text(text = "Benchmark Sample Target App") })
                ActivityList()
            }
        }
        lifecycleScope.launch {
            logCompilationStatus()
        }
    }

    @Composable
    fun ActivityList() {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = "This app runs macrobenchmark tests",
            textAlign = TextAlign.Center
        )
        Column {
            BenchmarkActivityButton(name = "Login") {
                launchActivityWithTrace<LoginActivity>()
            }
            BenchmarkActivityButton(name = "ListView") {
                launchActivityWithTrace<ListViewActivity>()
            }
            BenchmarkActivityButton(name = "Compose") {
                launchActivityWithTrace<ComposeActivity>()
            }
            BenchmarkActivityButton(name = "ScrollView") {
                launchActivityWithTrace<ScrollViewActivity>()
            }
            BenchmarkActivityButton(name = "Fully Drawn") {
                launchActivityWithTrace<FullyDrawnStartupActivity>()
            }
            BenchmarkActivityButton(
                name = "RecyclerView",
                "launchRecyclerActivity"
            ) {
                launchActivityWithTrace<NonExportedRecyclerActivity>()
            }
            BenchmarkActivityButton(
                name = "Nested RecyclerView",
                "nestedRecyclerActivity"
            ) {
                launchActivityWithTrace<NestedRecyclerActivity>()
            }
            BenchmarkActivityButton(
                name = "Nested RecyclerView with Pools",
                "nestedRecyclerWithPoolsActivity"
            ) {
                launchActivityWithTrace<NestedRecyclerActivity>(
                    Intent().putExtra(
                        USE_RECYCLER_POOLS, true
                    )
                )
            }
        }
    }

    @Composable
    fun BenchmarkActivityButton(name: String, testTag: String = "", onClick: () -> Unit) {
        TextButton(
            modifier = Modifier
                .padding(8.dp)
                .testTag(testTag),
            onClick = onClick,
            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
        ) {
            Text(name)
        }
    }

    private inline fun <reified T : Activity> launchActivityWithTrace(base: Intent? = null) {
        ClickTrace.onClickPerformed()
        val intent = Intent(this, T::class.java)
        if (base != null) {
            intent.putExtras(base)
        }
        startActivity(intent)
    }

    /**
     * Logs the app's Baseline Profile Compilation Status using [ProfileVerifier].
     */
    private suspend fun logCompilationStatus() {
        withContext(Dispatchers.IO) {

            /*
            To verify profile installation locally, you need to either compile the app
            with the speed-profile like so:
             `adb shell cmd package compile -f -m speed-profile com.example.macrobenchmark.target`

            Or trigger background dex optimizations manually like so:
            `adb shell pm bg-dexopt-job`
            As these run in the background it might take a while for this to complete.

            To see quick turnaround of the ProfileVerifier, we recommend using `speed-profile`.
            If you don't do either of these steps, you might only see the profile being enqueued for
            compilation when running the sample locally.
             */
            val status = ProfileVerifier.getCompilationStatusAsync().await()
            Log.d(TAG, "ProfileInstaller status code: ${status.profileInstallResultCode}")
            Log.d(
                TAG,
                when {
                    status.isCompiledWithProfile -> "ProfileInstaller: is compiled with profile"
                    status.hasProfileEnqueuedForCompilation() -> "ProfileInstaller: Enqueued for compilation"
                    else -> "Profile not compiled or enqueued"
                }
            )
        }
    }
}
