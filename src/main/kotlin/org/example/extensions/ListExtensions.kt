package org.example.extensions

/**
 * Возвращает медиану списка: средний элемент упорядоченной выборки, а для чётного размера —
 * среднее арифметическое двух средних.
 *
 * Исходный список не изменяется — сортировка выполняется на копии. Сумма двух средних элементов
 * считается в `Long`, иначе она переполнилась бы на больших значениях ещё до расширения до `Double`.
 *
 * @return медиана или `null`, если список пуст
 */
fun List<Int>.median(): Double? {
    if (isEmpty()) return null
    val sorted = sorted()
    val middle = size / 2
    return if (size % 2 == 1) {
        sorted[middle].toDouble()
    } else {
        (sorted[middle - 1].toLong() + sorted[middle]) / 2.0
    }
}
