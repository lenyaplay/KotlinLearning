package org.example.extensions

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ListExtensionsTest {
    @Test
    fun `median of odd sized list`() {
        assertEquals(5.0, listOf(5).median())
        assertEquals(2.0, listOf(1, 2, 3).median())
        assertEquals(-3.0, listOf(-5, -3, -1).median())
    }

    @Test
    fun `median of even sized list`() {
        assertEquals(1.5, listOf(1, 2).median())
        assertEquals(2.5, listOf(1, 2, 3, 4).median())
    }

    @Test
    fun `median sorts input without modifying it`() {
        assertEquals(2.0, listOf(3, 1, 2).median())
        assertEquals(2.5, listOf(4, 1, 3, 2).median())

        val source = listOf(3, 1, 2)
        source.median()
        assertEquals(listOf(3, 1, 2), source)
    }

    @Test
    fun `median of empty list is null`() {
        assertNull(emptyList<Int>().median())
    }
}
