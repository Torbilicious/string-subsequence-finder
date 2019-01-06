package de.torbilicious.subsequencefinder

import kotlin.system.measureTimeMillis

data class TestData(
    val s1: String,
    val s2: String,
    val expectedResult: String
)

object SubSequenceFinder {
    private const val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    private val tests = listOf(
        TestData("ABAZDC", "BACBAD", "ABAD"),
        TestData("AGGTAB", "GXTXAYB", "GTAB"),
        TestData("AAAA", "AA", "AA"),
        TestData("", "...", ""),
        TestData("ABBA", "ABCABA", "ABBA")
    )

    fun runTests() {
        val testResults = tests.map {
            var actual = ""
            val time = measureTimeMillis {
                actual = findLongestSubsequenceFor(it.s1, it.s2)
            }
            println("Expected: '${it.expectedResult}' | Actual: '$actual'")
            println("Calculated in ${time}ms")

            it.expectedResult == actual
        }

        println()
        println("Success: ${testResults.count { it }}")
        println("Failure: ${testResults.count { !it }}")
    }


    private fun findLongestSubsequenceFor(s1: String, s2: String): String {
        val sanitizedInput = prepareInput(s1, s2)

        val s1Candidates = getAllSubsequenceCandidates(sanitizedInput.first.toUpperCase()).toSet()
        val s2Candidates = getAllSubsequenceCandidates(sanitizedInput.second.toUpperCase()).toSet()

        val matchingStrings = s1Candidates.filter { s2Candidates.contains(it) }

        return if (matchingStrings.isEmpty()) {
            ""
        } else {
            matchingStrings.sortedBy { it.length }.reversed().first()
        }
    }

    fun getAllSubsequenceCandidates(string: String): List<String> {
        if (string.isEmpty()) return emptyList()

        val chars = string.toCharArray()
        val firstChar = chars.first()
        val stringWithoutFirst = string.removeRange(0..0)

        // All combinations of firstChar and the next characters in the string
        val combinationsThisLevel = stringWithoutFirst.toCharArray().map { firstChar.toString() + it }
        // recursive call with the first character of the input removed
        val combinationsForward = getAllSubsequenceCandidates(stringWithoutFirst)
        // Combine the result of the forward search with all resulsts of this level
        val thisAndNextCombined =
            combinationsThisLevel.flatMap { thisLevel ->
                combinationsForward.filter {
                    // Only combine if the first/last character matches
                    thisLevel.last() == it.first()
                }
                    .map { thisLevel + it.removeRange(0..0) }
            }

        return setOf(
            *combinationsThisLevel.toTypedArray(),
            *combinationsForward.toTypedArray(),
            *thisAndNextCombined.toTypedArray()
        ).toList()
    }

    private fun prepareInput(s1: String, s2: String): Pair<String, String> {
        val sanitizedS1 = s1.toUpperCase().toCharArray().filter { alphabet.contains(it) }.filter { s2.contains(it) }.joinToString("")
        val sanitizedS2 = s2.toUpperCase().toCharArray().filter { alphabet.contains(it) }.filter { s1.contains(it) }.joinToString("")

        return sanitizedS1 to sanitizedS2
    }
}

fun main(args: Array<String>) {
    SubSequenceFinder.runTests()
//    println(SubSequenceFinder.getAllForwardSequences("ABCD"))
}
