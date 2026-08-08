package org.example.extensions

fun List<Int>.median(): Double? {
    if (isEmpty()) return null
    val sorted = sorted()
    val middle = size / 2
    return if (size % 2 == 1) {
        sorted[middle].toDouble()
    } else {
        // toLong() обязателен: сумма двух больших Int переполнилась бы до расширения до Double
        (sorted[middle - 1].toLong() + sorted[middle]) / 2.0
    }
}
