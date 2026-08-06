package org.example.transformer

fun <T> mapList(input: List<T>, transform: (x : T) -> T): List<T> {
    val result = mutableListOf<T>()
    for (x in input) {
        result += transform(x)
    }
    return result
}