package org.example.extensions

/** Гласные латиницы и кириллицы; других письменностей задача не требует. */
private const val VOWELS = "aeiouyаеёиоуыэюяіїє"

/**
 * Считает гласные в строке без учёта регистра. Символы, не входящие в [VOWELS], игнорируются.
 *
 * @return число гласных букв
 */
fun String.vowelsCount(): Int {
    var count = 0
    for (ch in this) {
        if (isCharIsVowel(ch)) count++
    }
    return count
}

private fun isCharIsVowel(ch: Char): Boolean = ch.lowercaseChar() in VOWELS
