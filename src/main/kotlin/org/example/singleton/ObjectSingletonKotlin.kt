package org.example.singleton

import java.util.concurrent.atomic.AtomicInteger

/**
 * Идиоматичный Singleton в Kotlin — объявление `object`.
 *
 * Компилятор генерирует класс со статическим полем `INSTANCE`, заполняемым в статическом
 * инициализаторе, поэтому потокобезопасность и однократность создания обеспечивает сама JVM —
 * ровно как в holder-идиоме, но без единой строчки ручного кода. Инициализация ленивая: она
 * происходит при первом обращении к объекту, то есть при первой загрузке его класса.
 *
 * Ограничение: `object` не принимает параметров, поэтому для синглтона, которому нужен аргумент
 * времени выполнения, используется [DoubleCheckedSingletonKotlin].
 */
object ObjectSingletonKotlin {
    /** Счётчик вызовов инициализатора — тесты проверяют, что он равен 1. */
    val instantiationCount: AtomicInteger = AtomicInteger()

    init {
        instantiationCount.incrementAndGet()
    }

    /**
     * Точка доступа в терминах GoF. В Kotlin избыточна — к `object` обращаются просто по имени, —
     * но задание требует именно `getInstance()`.
     */
    fun getInstance(): ObjectSingletonKotlin = this
}
