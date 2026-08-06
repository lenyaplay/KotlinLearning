package org.example.singleton;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ленивый Singleton с синхронизированным {@link #getInstance()}.
 *
 * <p>Самый прямолинейный способ починить гонку: проверка и запись поля выполняются под общей
 * блокировкой, поэтому два потока не могут одновременно увидеть {@code null}.
 *
 * <p>Недостаток — блокировка берётся при каждом обращении, хотя реально нужна только при первом.
 * Исторически это и породило double-checked locking (см. {@link DoubleCheckedSingletonJava}),
 * хотя на современных JVM разница на неконкурентной блокировке невелика.
 */
public final class SynchronizedSingletonJava {
    public static final AtomicInteger instantiationCount = new AtomicInteger();

    private static SynchronizedSingletonJava instance;

    private SynchronizedSingletonJava() {
        instantiationCount.incrementAndGet();
    }

    public static synchronized SynchronizedSingletonJava getInstance() {
        if (instance == null) {
            instance = new SynchronizedSingletonJava();
        }
        return instance;
    }
}
