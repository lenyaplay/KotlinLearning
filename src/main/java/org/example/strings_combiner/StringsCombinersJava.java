package org.example.strings_combiner;

public final class StringsCombinersJava {
    private StringsCombinersJava() {
    }

    /**
     * Склеивает две строки, трактуя {@code null} как пустую строку.
     * Контракт совпадает с Kotlin-версией {@code combineTwoStrings} из StringsCombinersKotlin.kt.
     *
     * @param str1 первая строка, может быть {@code null}
     * @param str2 вторая строка, может быть {@code null}
     * @return конкатенация строк в порядке аргументов, никогда не {@code null}
     */
    public static String combineTwoStrings(String str1, String str2) {
        return (str1 == null ? "" : str1) + (str2 == null ? "" : str2);
    }
}
