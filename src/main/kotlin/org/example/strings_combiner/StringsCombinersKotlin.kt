package org.example.strings_combiner

/**
 * Склеивает две строки, трактуя `null` как пустую строку.
 *
 * Порядок аргументов сохраняется, пробелы не обрезаются.
 * Контракт совпадает с Java-версией [org.example.strings_combiner.StringsCombinersJava.combineTwoStrings].
 *
 * @param str1 первая строка, может быть `null`
 * @param str2 вторая строка, может быть `null`
 * @return конкатенация строк в порядке аргументов, никогда не `null`
 *
 * Примеры:
 * ```
 * combineTwoStrings("foo", "bar")  // "foobar"
 * combineTwoStrings(null, "bar")   // "bar"
 * combineTwoStrings(null, null)    // ""
 * ```
 */
fun combineTwoStrings(str1: String?, str2: String?): String = (str1 ?: "") + (str2 ?: "")
