package org.example.singleton

import java.util.concurrent.atomic.AtomicInteger

/**
 * Заведомо сломанные реализации Singleton. Живут в тестовом сорссете, потому что нужны только для
 * одного: проверить, что тест на потокобезопасность действительно ловит гонку, а не проходит всегда.
 *
 * Конструктор намеренно «работает» [CONSTRUCTION_MILLIS] миллисекунд — это ширина окна, в котором
 * другой поток успевает увидеть ещё не заполненное поле.
 */
internal const val CONSTRUCTION_MILLIS: Long = 2

/**
 * Наивная ленивая инициализация без синхронизации: `if (instance == null) instance = ...`.
 *
 * Гонка очевидна — несколько потоков одновременно проходят проверку на `null`, и каждый создаёт свой
 * экземпляр. Именно этот дефект ловит `SingletonTest`.
 *
 * Второй, менее заметный дефект того же кода — небезопасная публикация (поток может увидеть ссылку на
 * недостроенный объект из-за переупорядочивания записей). Тестом он здесь не воспроизводится: на x86
 * модель памяти достаточно сильна, чтобы такое переупорядочивание практически не проявлялось.
 */
class NaiveSingletonKotlin private constructor() {
    init {
        instantiationCount.incrementAndGet()
        Thread.sleep(CONSTRUCTION_MILLIS)
    }

    companion object {
        val instantiationCount: AtomicInteger = AtomicInteger()

        private var instance: NaiveSingletonKotlin? = null

        fun getInstance(): NaiveSingletonKotlin {
            val existing = instance
            if (existing != null) return existing
            val created = NaiveSingletonKotlin()
            instance = created
            return created
        }

        /** Сбрасывает состояние между итерациями теста. Вызывается, когда рабочие потоки уже завершены. */
        fun reset() {
            instance = null
            instantiationCount.set(0)
        }
    }
}

/**
 * `by lazy(LazyThreadSafetyMode.NONE)` — режим без какой-либо синхронизации.
 *
 * Документация прямо предупреждает, что использовать его можно только когда значение заведомо
 * инициализируется из одного потока. В многопоточном вызове он даёт ту же гонку, что и
 * [NaiveSingletonKotlin], — полезная демонстрация того, что «ленивый» не значит «потокобезопасный».
 */
class LazyNoneSingletonKotlin private constructor() {
    init {
        instantiationCount.incrementAndGet()
        Thread.sleep(CONSTRUCTION_MILLIS)
    }

    companion object {
        val instantiationCount: AtomicInteger = AtomicInteger()

        private var lazyInstance = lazy(LazyThreadSafetyMode.NONE) { LazyNoneSingletonKotlin() }

        fun getInstance(): LazyNoneSingletonKotlin = lazyInstance.value

        /** Сбрасывает состояние между итерациями теста. Вызывается, когда рабочие потоки уже завершены. */
        fun reset() {
            lazyInstance = lazy(LazyThreadSafetyMode.NONE) { LazyNoneSingletonKotlin() }
            instantiationCount.set(0)
        }
    }
}
