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

package com.example.macrobenchmark.target.recyclerview

import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class NestedRecyclerViewModel : ViewModel() {

    private val _items = MutableStateFlow(emptyList<ParentItem>())
    val items = _items.asStateFlow()

    // same random seed for each instance of ViewModel to generate the same items
    private val random = Random(0)

    init {
        _items.value = generateItems()
    }

    private fun generateItems() = List(PARENT_ITEM_COUNT) { parentId ->
        ParentItem(
            id = parentId.toString(),
            content = "Parent $parentId",
            children = generateChildren(parentId)
        )
    }

    private fun generateChildren(parentId: Int) = List(CHILD_ITEM_COUNT) { childId ->
        ChildItem(
            id = "${parentId}_${childId}",
            parentId = parentId.toString(),
            title = "Child $childId",
            subtitle = "Subtitle $parentId $childId",
            description = randomText(),
            color = generateColor(parentId, childId),
            inWishlist = random.nextBoolean(),
            likeState = LikeState.values().let { it[random.nextInt(it.size)] },
        )
    }


    private fun generateColor(parentId: Int, childId: Int) = ColorUtils.HSLToColor(
        floatArrayOf(
            parentId / PARENT_ITEM_COUNT.toFloat() * 360f,
            (CHILD_ITEM_COUNT - childId) / CHILD_ITEM_COUNT.toFloat(),
            0.5f
        )
    )


    private fun randomText(words: Int = 30): String {
        val builder = StringBuilder(words)

        repeat(words) { w ->
            if (w != 0) {
                builder.append(" ")
            }

            // generate one word
            repeat(random.nextInt(4, 9)) { builder.append(chars[random.nextInt(chars.size)]) }
        }

        return builder.toString()
    }
}

private val chars = List(26) { 'A' + it } + List(26) { 'a' + it }
private const val PARENT_ITEM_COUNT = 50
private const val CHILD_ITEM_COUNT = 60
