package org.example.extensions

fun List<Int>.median(): Double? {
    if (isEmpty()) return null
    val sorted = sorted()
    val middle = size / 2
    return if (size % 2 == 1) {
        sorted[middle].toDouble()
    } else {
        (sorted[middle - 1] + sorted[middle]) / 2.0
    }
}
