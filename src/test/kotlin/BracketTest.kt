import kotlinx.serialization.json.Json
import org.example.BracketsValidator
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals

class BracketTest {
    private fun readJson(): String {
        val inputStream = this::class.java.classLoader.getResourceAsStream("bracket_test_cases.json")
        val bufferedReader = inputStream?.bufferedReader()
        return bufferedReader?.use { it.readText() } ?: error("Could not read BracketTestCase file")
    }

    private fun loadTestCases(): List<BracketTestCase> = Json.decodeFromString(readJson())

    @TestFactory
    fun `валидатор проверяет скобочные последовательности`(): List<DynamicTest> =
        loadTestCases().map { testCase ->
            // каждый кейс — отдельный тест, чтобы падение одного не скрывало остальные
            dynamicTest("#${testCase.id} \"${testCase.input}\" — ${testCase.comment}") {
                assertEquals(testCase.expected, BracketsValidator.isValid(testCase.input))
            }
        }
}
