package org.example.transformer

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class ListMapperTest {
    @Test
    fun `multiply a list element by 2`() {
        val list = listOf(1, 2, 3)
        val mappedList = mapList(list) { x -> x * 2 }
        assertEquals(list.map { i -> i * 2}, mappedList)
    }

}