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

package com.example.benchmark.ui

object SortingAlgorithms {

    fun bubbleSort(array: IntArray) {
        var sorted = false
        var temp: Int
        while (!sorted) {
            sorted = true
            for (i in 0 until array.lastIndex) {
                if (array[i] > array[i + 1]) {
                    temp = array[i]
                    array[i] = array[i + 1]
                    array[i + 1] = temp
                    sorted = false
                }
            }
        }
    }

    fun quickSort(array: IntArray, begin: Int = 0, end: Int = array.lastIndex) {
        if (end <= begin) return
        val pivot = partition(array, begin, end)

        quickSort(array, begin, pivot - 1)
        quickSort(array, pivot + 1, end)
    }

    private fun partition(array: IntArray, begin: Int, end: Int): Int {
        var counter = begin
        for (i in begin until end) {
            if (array[i] < array[end]) {
                val temp = array[counter]
                array[counter] = array[i]
                array[i] = temp
                counter++
            }
        }
        val temp = array[end]
        array[end] = array[counter]
        array[counter] = temp
        return counter
    }
}

/**
 * Check if the array is already sorted
 */
val IntArray.isSorted
    get() = this
        .asSequence()
        .zipWithNext { a, b -> a <= b }
        .all { it }
