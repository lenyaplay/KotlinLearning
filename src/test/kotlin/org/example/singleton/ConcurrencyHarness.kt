package org.example.singleton

import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

/** Сколько ждать, пока все задачи дойдут до шлагбаума. */
private const val READY_TIMEOUT_SECONDS = 10L

/** Сколько ждать результат одной задачи после старта. */
private const val RESULT_TIMEOUT_SECONDS = 30L

/**
 * Запускает [action] в [threads] потоках так, чтобы вызовы стартовали максимально одновременно.
 *
 * Каждый поток сначала сообщает о своей готовности (`ready`), затем ждёт общий стартовый шлагбаум
 * (`start`). Пул успевает развернуть все потоки заранее, поэтому после единственного `countDown()`
 * они входят в [action] с разбросом в десятки микросекунд — этого достаточно, чтобы попасть в окно
 * гонки шириной в миллисекунды.
 *
 * Пул обязан вмещать [threads] одновременно работающих задач: задачи блокируются на шлагбауме, и если
 * часть из них останется в очереди, готовность не наступит никогда. Нарушение этого условия роняет
 * вызов по таймауту, а не подвешивает сборку.
 *
 * @return результаты вызовов **в порядке отправки задач** (не завершения)
 * @throws IllegalStateException если задачи не дошли до шлагбаума за [READY_TIMEOUT_SECONDS] секунд —
 *   как правило, из-за того, что пул меньше [threads]
 */
internal fun <T> ExecutorService.runConcurrently(threads: Int, action: () -> T): List<T> {
    require(threads > 0) { "нужен хотя бы один поток, получено $threads" }

    val ready = CountDownLatch(threads)
    val start = CountDownLatch(1)

    val tasks = List(threads) {
        Callable {
            ready.countDown()
            start.await()
            action()
        }
    }

    val futures = tasks.map { submit(it) }
    check(ready.await(READY_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
        "за $READY_TIMEOUT_SECONDS с до шлагбаума дошли не все задачи: " +
            "осталось ${ready.count} из $threads — вмещает ли пул $threads одновременных задач?"
    }
    start.countDown()
    return futures.map { it.get(RESULT_TIMEOUT_SECONDS, TimeUnit.SECONDS) }
}
