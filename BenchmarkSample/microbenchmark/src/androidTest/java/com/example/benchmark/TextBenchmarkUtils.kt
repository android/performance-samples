package com.example.benchmark

import kotlin.random.Random

// a small number of strings reused frequently, expected to hit
// in the word-granularity text layout cache
private val CACHE_HIT_STRINGS =
    arrayOf("a", "small", "number", "of", "strings", "reused", "frequently")

/**
 * This Random is reused by all benchmarks for selecting characters, so that each will select
 * different characters for each word, though the count and length of each word will be consistent.
 *
 * You can move this definition into the buildRandomParagraph function to see the affect of the
 * platform's text layout cache, when hitrate is artificially high due to string reuse.
 */
private val unstableRandom = Random(0)

private val chars: List<Char> = List(26) { 'a' + it } + List(26) { 'A' + it }

/**
 * Builds a random paragraph string of words of ascii letters.
 *
 * @param hitPercentage defines the percentage of frequently used words. These words are expected to
 * have their layout information cached by the platform, and to be fast - 30% is a very rough
 * estimate of the hit rate in practice in English with arbitrary sample text.
 *
 * @param wordsInParagraph length of the paragraph to generate..
 */
fun buildRandomParagraph(
    hitPercentage: Int = 30,
    wordsInParagraph: Int = 50
): String {
    require(!(hitPercentage < 0 || hitPercentage > 100))
    val stableRandom = Random(0)

    val result = StringBuilder()
    for (word in 0 until wordsInParagraph) {
        if (word != 0) {
            result.append(" ")
        }
        if (stableRandom.nextInt(100) < hitPercentage) {
            // add a common word, which is very likely to hit in the cache
            result.append(CACHE_HIT_STRINGS[stableRandom.nextInt(CACHE_HIT_STRINGS.size)])
        } else {
            // construct a random word, which will *most likely* miss in the layout cache
            for (j in 0 until stableRandom.nextInt(4, 9)) {
                // add random letter
                result.append(chars[unstableRandom.nextInt(chars.size)])
            }
        }
    }
    return result.toString()
}