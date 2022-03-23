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

package com.example.jankstats.tools

import androidx.tracing.trace
import kotlin.random.Random.Default.nextFloat
import kotlin.random.Random.Default.nextLong

/**
 * Inject random delay to cause jank in the app.
 * For any given item, there should be a 30% chance of jank (>32ms), and a 2% chance of
 * extreme jank (>500ms).
 * Regular jank will be between 32 and 82ms, extreme from 500-700ms.
 */
fun simulateJank(
    jankProbability: Double = 0.3,
    extremeJankProbability: Double = 0.02
) {
    val probability = nextFloat()

    if (probability > 1 - jankProbability) {
        val delay = if (probability > 1 - extremeJankProbability) {
            nextLong(500, 700)
        } else {
            nextLong(32, 82)
        }

        try {
            // Make jank easier to spot in the profiler through tracing.
            trace("Jank Simulation") {
                Thread.sleep(delay)
            }
        } catch (e: Exception) {
        }
    }
}
