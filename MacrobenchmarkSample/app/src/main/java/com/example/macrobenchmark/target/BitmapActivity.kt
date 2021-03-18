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

package com.example.macrobenchmark.target

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.trace

class BitmapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap)
        val id = intent.extras?.get(EXTRAS_IMAGE_ID) ?: SMALL_IMAGE_ID
        val image = findViewById<ImageView>(R.id.image)
        trace("Loading image bitmap") {
            val bitmap = when (id) {
                SMALL_IMAGE_ID -> BitmapFactory.decodeResource(
                    resources,
                    R.drawable.cityscape_small
                )
                else -> BitmapFactory.decodeResource(resources, R.drawable.cityscape_medium)
            }
            image.setImageBitmap(bitmap)
        }
    }

    companion object {
        private const val EXTRAS_IMAGE_ID = "EXTRAS_IMAGE_ID"
        private const val SMALL_IMAGE_ID = 0
    }
}
