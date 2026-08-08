package org.example.brackets

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals

class BracketsValidatorTest {
    private val implementations: List<Pair<String, (String) -> Boolean>> = listOf(
        "Kotlin" to BracketsValidatorKotlin::isValid,
        "Java" to BracketsValidatorJava::isValid,
    )

    private val testCases: List<BracketTestCase> by lazy { loadTestCases() }

    private fun readJson(): String {
        val inputStream = this::class.java.classLoader.getResourceAsStream("brackets/bracket_test_cases.json")
        val bufferedReader = inputStream?.bufferedReader()
        return bufferedReader?.use { it.readText() } ?: error("Could not read BracketTestCase file")
    }

    private fun loadTestCases(): List<BracketTestCase> = Json.decodeFromString(readJson())

    /** Каждый кейс — отдельный динамический тест, чтобы падение одного не скрывало остальные. */
    @TestFactory
    fun `validates bracket sequences from json cases`(): List<DynamicTest> =
        implementations.flatMap { (language, isValid) ->
            testCases.map { testCase ->
                dynamicTest("[$language] #${testCase.id} \"${testCase.input}\" — ${testCase.comment}") {
                    assertEquals(testCase.expected, isValid(testCase.input))
                }
            }
        }
}
