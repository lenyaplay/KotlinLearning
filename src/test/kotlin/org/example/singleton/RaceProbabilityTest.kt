package org.example.singleton

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Тесты самого расчёта: формула `R ≥ ln(1 - P) / ln(1 - p)` проверяется на значениях, посчитанных
 * вручную, — иначе на неё нельзя было бы опираться в проверках на антипаттернах.
 */
class RaceProbabilityTest {
    @Test
    fun `required iterations for 99 percent confidence`() {
        assertEquals(1, RaceProbability.requiredIterations(1.0)) // дефект ловится всегда
        assertEquals(2, RaceProbability.requiredIterations(0.9))
        assertEquals(7, RaceProbability.requiredIterations(0.5))
        assertEquals(21, RaceProbability.requiredIterations(0.2))
        assertEquals(90, RaceProbability.requiredIterations(0.05))
        assertEquals(459, RaceProbability.requiredIterations(0.01))
    }

    @Test
    fun `higher confidence requires more iterations`() {
        assertTrue(
            RaceProbability.requiredIterations(0.1, confidence = 0.999) >
                RaceProbability.requiredIterations(0.1, confidence = 0.99),
        )
    }

    @Test
    fun `required iterations really reach the requested confidence`() {
        for (p in listOf(0.9, 0.5, 0.2, 0.05, 0.01)) {
            val required = RaceProbability.requiredIterations(p)
            assertTrue(RaceProbability.detectionProbability(p, required) >= 0.99, "p = $p")
            // и это минимум: на одну итерацию меньше уже не хватает
            assertTrue(RaceProbability.detectionProbability(p, required - 1) < 0.99, "p = $p")
        }
    }

    @Test
    fun `detection probability grows with iterations`() {
        assertEquals(0.0, RaceProbability.detectionProbability(0.0, 1000))
        assertEquals(0.5, RaceProbability.detectionProbability(0.5, 1))
        assertTrue(RaceProbability.detectionProbability(0.5, 10) > RaceProbability.detectionProbability(0.5, 5))
        assertEquals(1.0, RaceProbability.detectionProbability(1.0, 1))
    }

    @Test
    fun `undetectable defect is rejected`() {
        assertFailsWith<IllegalArgumentException> { RaceProbability.requiredIterations(0.0) }
        assertFailsWith<IllegalArgumentException> { RaceProbability.requiredIterations(0.5, confidence = 1.0) }
        assertFailsWith<IllegalArgumentException> { RaceProbability.detectionProbability(1.5, 10) }
    }

    @Test
    fun `report summarizes a run`() {
        val report = RaceReport(name = "Demo", iterations = 500, failures = 250)

        assertEquals(0.5, report.observedProbability)
        assertEquals(7, report.requiredIterations)
        assertTrue(report.confidence > 0.99)
    }
}
