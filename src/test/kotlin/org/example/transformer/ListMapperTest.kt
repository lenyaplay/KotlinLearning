package org.example.transformer

import kotlin.test.Test
import kotlin.test.assertEquals

class ListMapperTest {
    @Test
    fun `kotlin mapList maps elements`() {
        assertEquals(listOf(2, 4, 6), mapList(listOf(1, 2, 3)) { x -> x * 2 })
        assertEquals(emptyList(), mapList(emptyList<Int>()) { x -> x * 2 })
        assertEquals(listOf(42), mapList(listOf(42)) { x -> x })

        val source = listOf(1, 2, 3)
        mapList(source) { x -> x * 2 }
        assertEquals(listOf(1, 2, 3), source)
    }

    @Test
    fun `java mapList maps elements`() {
        assertEquals(listOf(2, 4, 6), TransformersJava.mapList(listOf(1, 2, 3)) { x -> x * 2 })
        assertEquals(emptyList(), TransformersJava.mapList(emptyList<Int>()) { x -> x * 2 })
        assertEquals(listOf(42), TransformersJava.mapList(listOf(42)) { x -> x })

        val source = listOf(1, 2, 3)
        TransformersJava.mapList(source) { x -> x * 2 }
        assertEquals(listOf(1, 2, 3), source)
    }
}
