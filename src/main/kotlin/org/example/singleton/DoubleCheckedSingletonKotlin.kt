package org.example.singleton

import java.util.concurrent.atomic.AtomicInteger

/**
 * Ленивый Singleton с double-checked locking и параметром времени выполнения.
 *
 * Это тот случай, ради которого DCL в Kotlin вообще нужен: `object` параметров не принимает, а здесь
 * экземпляр создаётся с [tag], известным только в момент первого вызова. На Android так обычно
 * выглядит `getInstance(context)`.
 *
 * Аннотация `@Volatile` обязательна и защищает от небезопасной публикации: без неё другой поток на
 * первой (неблокирующей) проверке может увидеть ссылку на объект, конструктор которого ещё не
 * завершился.
 *
 * Поведение при повторных вызовах повторяет обычную практику: [tag] учитывается только при первом
 * вызове, дальше возвращается уже созданный экземпляр.
 */
class DoubleCheckedSingletonKotlin private constructor(val tag: String) {
    init {
        instantiationCount.incrementAndGet()
    }

    companion object {
        val instantiationCount: AtomicInteger = AtomicInteger()

        @Volatile
        private var instance: DoubleCheckedSingletonKotlin? = null

        /**
         * @param tag значение, с которым будет создан экземпляр при первом вызове
         * @return единственный экземпляр; при повторных вызовах [tag] игнорируется
         */
        fun getInstance(tag: String): DoubleCheckedSingletonKotlin {
            // локальная копия: одно чтение volatile-поля вместо двух на быстром пути
            instance?.let { return it }
            return synchronized(this) {
                instance ?: DoubleCheckedSingletonKotlin(tag).also { instance = it }
            }
        }
    }
}
