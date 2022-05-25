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

package com.example.macrobenchmark.target

import android.os.Looper
import android.view.MotionEvent
import androidx.tracing.Trace
import curtains.Curtains
import curtains.OnRootViewAddedListener
import curtains.OnTouchEventListener
import curtains.phoneWindow
import curtains.touchEventInterceptors

/**
 * Logs async trace sections that track the duration of click handling in the app, i.e. the
 * duration from MotionEvent.ACTION_UP to OnClick callbacks.
 */
object ClickTrace {

    private const val SECTION_NAME = "ClickTrace"

    private var clickInProgress = false

    private val isMainThread: Boolean get() = Looper.getMainLooper().thread === Thread.currentThread()

    fun onClickPerformed() {
        Trace.endAsyncSection(SECTION_NAME, 0)
        checkMainThread()
        check(clickInProgress) {
            "No click in progress"
        }
        clickInProgress = false
    }

    fun install() {
        checkMainThread()
        Curtains.onRootViewsChangedListeners += OnRootViewAddedListener { rootView ->
            rootView.phoneWindow?.let { window ->
                window.touchEventInterceptors += OnTouchEventListener { event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        Trace.beginAsyncSection(SECTION_NAME, 0)
                        check(!clickInProgress) {
                            "Click already in progress, chill!"
                        }
                        clickInProgress = true
                    }
                }
            }
        }
    }

    private fun checkMainThread() {
        check(isMainThread) {
            "Should be called from the main thread, not ${Thread.currentThread()}"
        }
    }
}