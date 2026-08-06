package org.example.singleton;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton с «жадной» (eager) инициализацией: экземпляр создаётся при загрузке класса.
 *
 * <p>Потокобезопасен без единой строчки синхронизации: JVM гарантирует, что статические инициализаторы
 * класса выполняются ровно один раз и что результат виден всем потокам. Плата — отсутствие ленивости:
 * объект создаётся, даже если {@link #getInstance()} так и не вызовут.
 */
public final class EagerSingletonJava {
    /** Счётчик вызовов конструктора — для проверки в тестах, что экземпляр создан ровно один раз. */
    public static final AtomicInteger instantiationCount = new AtomicInteger();

    private static final EagerSingletonJava INSTANCE = new EagerSingletonJava();

    private EagerSingletonJava() {
        instantiationCount.incrementAndGet();
    }

    public static EagerSingletonJava getInstance() {
        return INSTANCE;
    }
}
