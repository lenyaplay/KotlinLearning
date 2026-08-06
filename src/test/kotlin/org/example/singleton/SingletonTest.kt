package org.example.singleton

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

private const val THREADS = 64

/**
 * Число итераций для проверок на антипаттернах. Обосновано в [RaceProbability]: даже при пессимистичном
 * `p = 0.01` для 99%-й гарантии хватает 459 итераций. Что взятого числа достаточно при **измеренной**
 * вероятности, каждый такой тест проверяет отдельным утверждением.
 */
private const val ITERATIONS = 500

/**
 * Один набор тестов на все реализации Singleton: корректные проходят проверку [assertSingleInstance],
 * заведомо сломанные обязаны её проваливать.
 *
 * Проверка везде одна и та же, отличается только ожидаемый исход — поэтому она заодно доказывает, что
 * положительные тесты не проходят «сами собой».
 */
class SingletonTest {
    private val pool: ExecutorService = Executors.newFixedThreadPool(THREADS)

    @AfterTest
    fun tearDown() {
        pool.shutdownNow()
    }

    /**
     * Общая проверка контракта Singleton: [THREADS] одновременных вызовов возвращают один и тот же
     * объект, повторные вызовы — тоже, а конструктор отработал ровно один раз.
     *
     * Сравнение по identity ([assertSame]), а не по `equals`: смысл паттерна в том, что объект физически один.
     *
     * Конкурентная часть идёт первой: последовательный вызов создал бы экземпляр заранее и закрыл окно
     * гонки, из-за чего сломанная реализация прошла бы проверку.
     */
    private fun <T> assertSingleInstance(instantiationCount: () -> Int, getInstance: () -> T) {
        val instances = pool.runConcurrently(THREADS) { getInstance() }
        val first = instances.first()
        instances.forEach { assertSame(first, it) }

        assertSame(first, getInstance())
        assertEquals(1, instantiationCount())
    }

    /**
     * Прогоняет [assertSingleInstance] против сломанной реализации [ITERATIONS] раз и требует, чтобы
     * проверка падала.
     *
     * Проверяется не только сам факт обнаружения, но и достаточность числа итераций: измеренная
     * вероятность подставляется в [RaceProbability.requiredIterations], и тест падает, если [ITERATIONS]
     * оказалось меньше нужного для 99%-й гарантии.
     */
    private fun <T> assertRaceIsDetected(
        name: String,
        reset: () -> Unit,
        instantiationCount: () -> Int,
        getInstance: () -> T,
    ) {
        var failures = 0
        repeat(ITERATIONS) {
            reset()
            val error = runCatching { assertSingleInstance(instantiationCount, getInstance) }.exceptionOrNull()
            if (error is AssertionError) failures++ else if (error != null) throw error
        }

        val report = RaceReport(name, ITERATIONS, failures)
        println(report)

        assertTrue(failures > 0, "$name: гонка не обнаружена ни разу за $ITERATIONS итераций — проверка бесполезна")
        val required = report.requiredIterations!!
        assertTrue(
            ITERATIONS >= required,
            "$name: при p = ${report.observedProbability} для 99%-й гарантии нужно $required итераций, " +
                "а выполнено только $ITERATIONS",
        )
    }

    // --- корректные реализации: проверка должна проходить ---

    @Test
    fun `java eager singleton`() =
        assertSingleInstance({ EagerSingletonJava.instantiationCount.get() }, EagerSingletonJava::getInstance)

    @Test
    fun `java synchronized singleton`() =
        assertSingleInstance(
            { SynchronizedSingletonJava.instantiationCount.get() },
            SynchronizedSingletonJava::getInstance,
        )

    @Test
    fun `java double checked singleton`() =
        assertSingleInstance(
            { DoubleCheckedSingletonJava.instantiationCount.get() },
            DoubleCheckedSingletonJava::getInstance,
        )

    @Test
    fun `java holder singleton`() =
        assertSingleInstance({ HolderSingletonJava.instantiationCount.get() }, HolderSingletonJava::getInstance)

    @Test
    fun `java enum singleton`() =
        assertSingleInstance({ EnumSingletonJava.instantiationCount().get() }, EnumSingletonJava::getInstance)

    @Test
    fun `kotlin object singleton`() =
        assertSingleInstance({ ObjectSingletonKotlin.instantiationCount.get() }, ObjectSingletonKotlin::getInstance)

    @Test
    fun `kotlin lazy singleton`() =
        assertSingleInstance({ LazySingletonKotlin.instantiationCount.get() }, LazySingletonKotlin::getInstance)

    @Test
    fun `kotlin synchronized singleton`() =
        assertSingleInstance(
            { SynchronizedSingletonKotlin.instantiationCount.get() },
            SynchronizedSingletonKotlin::getInstance,
        )

    @Test
    fun `kotlin double checked singleton`() {
        assertSingleInstance({ DoubleCheckedSingletonKotlin.instantiationCount.get() }) {
            DoubleCheckedSingletonKotlin.getInstance("first")
        }
        // параметр учитывается только при первом вызове
        assertEquals("first", DoubleCheckedSingletonKotlin.getInstance("second").tag)
    }

    // --- антипаттерны: та же проверка обязана падать ---

    @Test
    fun `naive lazy initialization fails the check`() =
        assertRaceIsDetected(
            name = "NaiveSingletonKotlin",
            reset = NaiveSingletonKotlin::reset,
            instantiationCount = { NaiveSingletonKotlin.instantiationCount.get() },
            getInstance = NaiveSingletonKotlin::getInstance,
        )

    @Test
    fun `lazy with thread safety mode NONE fails the check`() =
        assertRaceIsDetected(
            name = "LazyNoneSingletonKotlin",
            reset = LazyNoneSingletonKotlin::reset,
            instantiationCount = { LazyNoneSingletonKotlin.instantiationCount.get() },
            getInstance = LazyNoneSingletonKotlin::getInstance,
        )
}
