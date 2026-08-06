package org.example.singleton;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton на enum — вариант, который рекомендует Джошуа Блох в «Effective Java».
 *
 * <p>Потокобезопасен по тем же причинам, что и {@link EagerSingletonJava} (константы enum
 * инициализируются в статическом инициализаторе класса), но дополнительно защищён от двух обходов,
 * против которых обычный Singleton беспомощен: рефлексии ({@code Constructor.newInstance} на enum
 * бросает {@link IllegalArgumentException}) и десериализации (JVM сама возвращает существующую
 * константу, а не создаёт новый объект).
 *
 * <p>Минусы: инициализация не ленивая и нельзя наследоваться от другого класса.
 */
public enum EnumSingletonJava {
    INSTANCE;

    EnumSingletonJava() {
        Counter.instantiationCount.incrementAndGet();
    }

    public static EnumSingletonJava getInstance() {
        return INSTANCE;
    }

    public static AtomicInteger instantiationCount() {
        return Counter.instantiationCount;
    }

    /**
     * Счётчик вынесен в отдельный класс намеренно: константы enum инициализируются раньше остальных
     * статических полей своего класса, поэтому обычное статическое поле было бы ещё {@code null}
     * в момент вызова конструктора.
     */
    private static final class Counter {
        private static final AtomicInteger instantiationCount = new AtomicInteger();
    }
}
