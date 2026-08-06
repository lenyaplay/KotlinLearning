package org.example.singleton

import java.util.concurrent.atomic.AtomicInteger

/**
 * Ленивый Singleton с полностью синхронизированным `getInstance()`.
 *
 * Kotlin-аналог [org.example.singleton.SynchronizedSingletonJava]: `@Synchronized` компилируется в
 * тот же `synchronized`-метод. Корректно, но блокировка берётся при каждом обращении, а не только при
 * первом — за ленивостью без этой платы стоит идти к `object` или `by lazy`.
 */
class SynchronizedSingletonKotlin private constructor() {
    init {
        instantiationCount.incrementAndGet()
    }

    companion object {
        val instantiationCount: AtomicInteger = AtomicInteger()

        private var instance: SynchronizedSingletonKotlin? = null

        @Synchronized
        fun getInstance(): SynchronizedSingletonKotlin =
            instance ?: SynchronizedSingletonKotlin().also { instance = it }
    }
}
