package org.example.singleton

import java.util.concurrent.atomic.AtomicInteger

/**
 * Ленивый Singleton через делегат `by lazy`.
 *
 * По умолчанию `lazy` работает в режиме [LazyThreadSafetyMode.SYNCHRONIZED]: инициализатор
 * выполняется под блокировкой и ровно один раз, остальные потоки ждут и получают готовое значение.
 * Внутри это тот же double-checked locking, что и в [DoubleCheckedSingletonKotlin], только написанный
 * за нас стандартной библиотекой.
 *
 * Режим [LazyThreadSafetyMode.NONE] такой гарантии не даёт и в многопоточном коде создаёт ровно ту
 * же гонку, что и наивная реализация, — это проверяется отдельным тестом на антипаттерны.
 */
class LazySingletonKotlin private constructor() {
    init {
        instantiationCount.incrementAndGet()
    }

    companion object {
        val instantiationCount: AtomicInteger = AtomicInteger()

        // имя не `instance`: у делегированного свойства есть JVM-геттер `getInstance()`,
        // который столкнулся бы с методом ниже
        private val lazyInstance: LazySingletonKotlin by lazy { LazySingletonKotlin() }

        fun getInstance(): LazySingletonKotlin = lazyInstance
    }
}
