package org.example.singleton

import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow

/**
 * Расчёт числа итераций, нужного, чтобы гонка гарантированно проявилась.
 *
 * Пусть `p` — вероятность, что одна итерация обнаружит дефект. Состояние синглтона сбрасывается между
 * итерациями, поэтому они независимы, и вероятность **не** обнаружить дефект за `R` итераций равна
 * `(1 - p)^R`. Отсюда вероятность обнаружения и требуемое число итераций:
 *
 * ```
 * P(R) = 1 - (1 - p)^R      =>      R ≥ ln(1 - P) / ln(1 - p)
 * ```
 */
object RaceProbability {
    /** Вероятность обнаружить дефект хотя бы раз за [iterations] итераций при вероятности [p] за итерацию. */
    fun detectionProbability(p: Double, iterations: Int): Double {
        require(p in 0.0..1.0) { "p должно быть в [0, 1], получено $p" }
        return 1 - (1 - p).pow(iterations)
    }

    /**
     * Минимальное число итераций, при котором дефект будет обнаружен с вероятностью не ниже [confidence].
     *
     * @param p вероятность обнаружения за одну итерацию; при `p = 1` достаточно одной итерации
     * @throws IllegalArgumentException если `p = 0` — такой дефект не обнаруживается никогда
     */
    fun requiredIterations(p: Double, confidence: Double = 0.99): Int {
        require(p > 0.0) { "при p = 0 дефект не обнаруживается ни за какое число итераций" }
        require(p <= 1.0) { "p должно быть в (0, 1], получено $p" }
        require(confidence > 0.0 && confidence < 1.0) { "confidence должно быть в (0, 1), получено $confidence" }
        if (p == 1.0) return 1
        return ceil(ln(1 - confidence) / ln(1 - p)).toInt()
    }
}

/**
 * Результат прогона проверки против заведомо сломанной реализации.
 *
 * @param name имя проверяемой реализации
 * @param iterations сколько итераций выполнено
 * @param failures в скольких из них проверка упала, то есть дефект был обнаружен
 */
data class RaceReport(val name: String, val iterations: Int, val failures: Int) {
    /** Измеренная вероятность обнаружения за одну итерацию. */
    val observedProbability: Double = failures.toDouble() / iterations

    /** Вероятность того, что выполненного числа итераций хватило бы для обнаружения дефекта. */
    val confidence: Double = RaceProbability.detectionProbability(observedProbability, iterations)

    /** Сколько итераций нужно для 99%-й гарантии при измеренной вероятности. */
    val requiredIterations: Int?
        get() = if (observedProbability > 0.0) RaceProbability.requiredIterations(observedProbability) else null

    override fun toString(): String =
        "$name: дефект обнаружен в $failures из $iterations итераций (p = $observedProbability), " +
            "P(R=$iterations) = $confidence, для 99% хватило бы R = ${requiredIterations ?: "недостижимо"}"
}
