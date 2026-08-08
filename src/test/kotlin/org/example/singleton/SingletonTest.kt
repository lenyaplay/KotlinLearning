package org.example.singleton

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInstance
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

private const val THREADS = 64

/**
 * Число итераций для проверок на антипаттернах. Обосновано через [RaceProbability]: 100 итераций дают
 * 99%-ю гарантию обнаружения при `p ≥ 0.045` (`requiredIterations(0.05) == 90`). Измеренное на этом
 * коде `p` равно 1.0, то есть запас двукратный по порядку величины.
 *
 * Автоматически проверить достаточность нельзя: любое утверждение вида `ITERATIONS >= required`
 * циклично, поскольку `required` считается по измеренной здесь же доле падений. Поэтому число
 * зафиксировано осознанно, а тест печатает измеренное `p` — если оно окажется ниже 0.045, константу
 * нужно поднимать.
 */
private const val ITERATIONS = 100

/**
 * Один набор тестов на все реализации Singleton: корректные проходят проверку [assertSingleInstance],
 * заведомо сломанные обязаны её проваливать.
 *
 * Проверка везде одна и та же, отличается только ожидаемый исход — поэтому она заодно доказывает, что
 * положительные тесты не проходят «сами собой».
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SingletonTest {
    // пул создаётся один раз на класс: при жизненном цикле по умолчанию (экземпляр на каждый тест)
    // получалось бы 11 × THREADS платформенных потоков за прогон
    private val pool: ExecutorService = Executors.newFixedThreadPool(THREADS)

    // @AfterAll, а не @AfterTest: пул общий для всех тестов класса и должен пережить их все
    @AfterAll
    fun tearDown() {
        pool.shutdown()
        check(pool.awaitTermination(30, TimeUnit.SECONDS)) { "пул не завершился за 30 с" }
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
     * проверка падала именно из-за гонки.
     *
     * Засчитывается не любое падение: обычный `AssertionError` мог бы прийти и от постороннего
     * утверждения (например, от испорченного `reset()`), поэтому дополнительно проверяется, что
     * конструктор действительно отработал больше одного раза.
     *
     * `ExecutionException(NullPointerException)` — тоже проявление гонки, а не постороннее падение:
     * `UnsafeLazyImpl` обнуляет `initializer` после инициализации, и поток, вошедший в него следом,
     * вычисляет `initializer!!()` уже на `null`.
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
            val raceDetected = when {
                error == null -> false
                error is AssertionError && instantiationCount() > 1 -> true
                error is ExecutionException && error.cause is NullPointerException -> true
                else -> throw error
            }
            if (raceDetected) failures++
        }

        println(RaceReport(name, ITERATIONS, failures))
        assertTrue(failures > 0, "$name: гонка не обнаружена ни разу за $ITERATIONS итераций — проверка бесполезна")
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
        // лямбда, а не ObjectSingletonKotlin::getInstance: bound callable reference компилируется в
        // getstatic INSTANCE и создал бы синглтон ещё до входа в проверку, закрыв окно гонки
        assertSingleInstance({ ObjectSingletonKotlin.instantiationCount.get() }) { ObjectSingletonKotlin.getInstance() }

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
