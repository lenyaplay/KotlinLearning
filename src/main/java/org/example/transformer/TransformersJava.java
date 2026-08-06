package org.example.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class TransformersJava {
    private TransformersJava() {
    }

    /**
     * Применяет преобразование к каждому элементу списка и возвращает новый список.
     * Контракт совпадает с Kotlin-версией {@code mapList} из TransformersKotlin.kt.
     *
     * @param input     исходный список, не изменяется
     * @param transform преобразование элемента
     * @return новый список с результатами преобразования, в исходном порядке
     */
    public static <T> List<T> mapList(List<T> input, Function<T, T> transform) {
        var result = new ArrayList<T>(input.size());
        for (var x : input) {
            result.add(transform.apply(x));
        }
        return result;
    }
}
