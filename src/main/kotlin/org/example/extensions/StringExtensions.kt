package org.example.extensions

//только латиница и кириллица — этого достаточно для текущей задачи
private const val VOWELS = "aeiouyаеёиоуыэюяіїє"

fun String.vowelsCount(): Int {
    var count = 0
    for (ch in this) {
        if (isCharIsVowel(ch)) count++
    }
    return count
}

private fun isCharIsVowel(ch: Char): Boolean = ch.lowercaseChar() in VOWELS
