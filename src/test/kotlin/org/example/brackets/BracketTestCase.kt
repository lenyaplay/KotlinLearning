package org.example.brackets

import kotlinx.serialization.Serializable

@Serializable
data class BracketTestCase (
    val id : Int,
    val input: String,
    val expected: Boolean,
    val comment: String
)
