package org.example.strings

import kotlin.test.Test
import kotlin.test.assertEquals

class StringsCombinersTest {
    @Test
    fun `kotlin combineTwoStrings concatenates strings`() {
        assertEquals("foobar", combineTwoStrings("foo", "bar"))
        assertEquals("bar", combineTwoStrings("", "bar"))
        assertEquals("foo", combineTwoStrings("foo", ""))
        assertEquals("", combineTwoStrings("", ""))

        assertEquals("bar", combineTwoStrings(null, "bar"))
        assertEquals("foo", combineTwoStrings("foo", null))
        assertEquals("", combineTwoStrings(null, null))

        assertEquals("ab", combineTwoStrings("a", "b"))
        assertEquals("foo  bar", combineTwoStrings("foo ", " bar"))
    }

    @Test
    fun `java combineTwoStrings concatenates strings`() {
        assertEquals("foobar", StringsCombinersJava.combineTwoStrings("foo", "bar"))
        assertEquals("bar", StringsCombinersJava.combineTwoStrings("", "bar"))
        assertEquals("foo", StringsCombinersJava.combineTwoStrings("foo", ""))
        assertEquals("", StringsCombinersJava.combineTwoStrings("", ""))

        assertEquals("bar", StringsCombinersJava.combineTwoStrings(null, "bar"))
        assertEquals("foo", StringsCombinersJava.combineTwoStrings("foo", null))
        assertEquals("", StringsCombinersJava.combineTwoStrings(null, null))

        assertEquals("ab", StringsCombinersJava.combineTwoStrings("a", "b"))
        assertEquals("foo  bar", StringsCombinersJava.combineTwoStrings("foo ", " bar"))
    }
}
