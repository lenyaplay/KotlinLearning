package org.example.singleton

import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService

/**
 * Запускает [action] в [threads] потоках так, чтобы вызовы стартовали максимально одновременно.
 *
 * Каждый поток сначала сообщает о своей готовности (`ready`), затем ждёт общий стартовый шлагбаум
 * (`start`). Пул успевает развернуть все потоки заранее, поэтому после единственного `countDown()`
 * они входят в [action] с разбросом в десятки микросекунд — этого достаточно, чтобы попасть в окно
 * гонки шириной в миллисекунды.
 *
 * Пул обязан вмещать [threads] одновременно работающих задач: задачи блокируются на шлагбауме, и если
 * часть из них останется в очереди, ожидание готовности не завершится никогда.
 *
 * @return результаты вызовов в порядке завершения задач
 */
fun <T> ExecutorService.runConcurrently(threads: Int, action: () -> T): List<T> {
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
    ready.await() // все потоки развёрнуты и ждут шлагбаума
    start.countDown()
    return futures.map { it.get() }
}
