package org.example.singleton;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ленивый Singleton с double-checked locking.
 *
 * <p>Первая проверка выполняется без блокировки — это быстрый путь для всех обращений после
 * инициализации. Блокировка берётся, только если поле ещё пустое, и внутри блокировки проверка
 * повторяется: пока поток ждал монитор, другой поток мог успеть создать экземпляр.
 *
 * <p>Ключевое слово {@code volatile} обязательно и решает вторую, менее очевидную проблему —
 * небезопасную публикацию. Выражение {@code new DoubleCheckedSingletonJava()} состоит из выделения
 * памяти, работы конструктора и присваивания ссылки; без {@code volatile} модель памяти Java
 * разрешает переупорядочить два последних шага, и другой поток на первой (неблокирующей) проверке
 * может увидеть ненулевую ссылку на ещё не достроенный объект. {@code volatile} запрещает такое
 * переупорядочивание и гарантирует видимость записанных в конструкторе полей.
 *
 * <p>Поле читается в локальную переменную, чтобы на быстром пути было одно чтение {@code volatile},
 * а не два.
 */
public final class DoubleCheckedSingletonJava {
    public static final AtomicInteger instantiationCount = new AtomicInteger();

    private static volatile DoubleCheckedSingletonJava instance;

    private DoubleCheckedSingletonJava() {
        instantiationCount.incrementAndGet();
    }

    public static DoubleCheckedSingletonJava getInstance() {
        DoubleCheckedSingletonJava result = instance;
        if (result == null) {
            synchronized (DoubleCheckedSingletonJava.class) {
                result = instance;
                if (result == null) {
                    result = new DoubleCheckedSingletonJava();
                    instance = result;
                }
            }
        }
        return result;
    }
}
