package org.example.singleton;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ленивый Singleton по идиоме «initialization-on-demand holder».
 *
 * <p>Вложенный класс {@code Holder} загружается не вместе с внешним классом, а только при первом
 * обращении к нему — то есть при первом вызове {@link #getInstance()}. Ленивость и потокобезопасность
 * при этом обеспечивает сама JVM: спецификация гарантирует, что инициализация класса выполняется
 * ровно один раз и под внутренней блокировкой загрузчика.
 *
 * <p>Обычно считается лучшим решением в Java: ленивое, без синхронизации на быстром пути и без
 * тонкостей {@code volatile}. Ограничение — не подходит, если экземпляр нужно создавать с параметрами,
 * известными только в рантайме (для этого нужен {@link DoubleCheckedSingletonJava}).
 */
public final class HolderSingletonJava {
    public static final AtomicInteger instantiationCount = new AtomicInteger();

    private HolderSingletonJava() {
        instantiationCount.incrementAndGet();
    }

    private static final class Holder {
        private static final HolderSingletonJava INSTANCE = new HolderSingletonJava();
    }

    public static HolderSingletonJava getInstance() {
        return Holder.INSTANCE;
    }
}
