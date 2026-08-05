import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlin.test.Test

class BracketTest {
    fun readJson() : String {
        val inputStream = this::class.java.classLoader.getResourceAsStream("bracket_test_cases.json")
        val bufferedReader =  inputStream?.bufferedReader();
        return bufferedReader?.use {it.readText() } ?: error("Could not read BracketTestCase file");
    }

    @Test
    fun `test parsing`() {
        val json = readJson();
        val testCases = Json.decodeFromString<List<BracketTestCase>>(json);
        testCases.forEach { testCase -> println(testCase.toString()) }
    }
}