package org.example.extensions

import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtensionsTest {
    @Test
    fun `counts latin and cyrillic vowels`() {
        assertEquals(0, "".vowelsCount())
        assertEquals(0, "bcdfg".vowelsCount())
        assertEquals(0, "123 !?-".vowelsCount())

        assertEquals(5, "aeiou".vowelsCount())
        assertEquals(4, "Hello Kotlin!".vowelsCount())
        assertEquals(1, "myth".vowelsCount())
        assertEquals(10, "аеёиоуыэюя".vowelsCount())
        assertEquals(3, "Привет, мир".vowelsCount())

        assertEquals("aeiou".vowelsCount(), "AEIOU".vowelsCount())
        assertEquals("аеёиоуыэюя".vowelsCount(), "АЕЁИОУЫЭЮЯ".vowelsCount())
    }
}
