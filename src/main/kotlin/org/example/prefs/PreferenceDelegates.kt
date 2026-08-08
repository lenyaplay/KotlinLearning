package org.example.prefs

import android.content.SharedPreferences
import java.util.Collections
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Делегат свойства поверх [SharedPreferences]: чтение свойства читает значение из префов,
 * запись — кладёт его обратно.
 *
 * Значение не кэшируется: каждое обращение к свойству идёт в [SharedPreferences], поэтому изменения,
 * сделанные в обход делегата (другой поток, `clear()`, другой процесс), видны сразу.
 *
 * @param prefs хранилище, в которое пишем и из которого читаем
 * @param key ключ в хранилище; имя свойства не используется, ключ всегда задаётся явно
 * @param default значение, возвращаемое, если ключа в хранилище нет
 * @param commit `true` — фиксировать запись синхронно через `commit()`, `false` — через `apply()`
 * @param read как прочитать значение из хранилища
 * @param write как записать значение в [SharedPreferences.Editor]
 */
private class PreferenceDelegate<T>(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: T,
    private val commit: Boolean,
    private val read: SharedPreferences.(String, T) -> T,
    private val write: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor,
) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = prefs.read(key, default)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val editor = prefs.edit().write(key, value)
        if (commit) editor.commit() else editor.apply()
    }
}

/**
 * Делегат для строкового значения.
 *
 * Повторяет семантику [SharedPreferences.getString]: если ключа нет, возвращается [default]
 * (по умолчанию `null`). Запись `null` удаляет ключ из хранилища — так же, как
 * `putString(key, null)`.
 *
 * Ключ должен быть уникален в пределах одного хранилища: за этим следит вызывающий код,
 * делегат никаких проверок не делает.
 *
 * @param key ключ в хранилище
 * @param default значение при отсутствии ключа
 * @param commit `true` — синхронная запись через `commit()`, по умолчанию используется `apply()`
 * @return делегат для свойства типа `String?`
 *
 * Пример:
 * ```
 * class Settings(prefs: SharedPreferences) {
 *     var token: String? by prefs.string("auth_token")
 *     var launches: Int by prefs.int("launches", 0)
 * }
 * ```
 */
fun SharedPreferences.string(
    key: String,
    default: String? = null,
    commit: Boolean = false,
): ReadWriteProperty<Any?, String?> = PreferenceDelegate(
    prefs = this,
    key = key,
    default = default,
    commit = commit,
    read = { k, d -> getString(k, d) },
    write = { k, v -> putString(k, v) },
)

/**
 * Делегат для набора строк.
 *
 * И на чтении, и на записи делается защитная копия: [SharedPreferences.getStringSet] возвращает набор,
 * который запрещено изменять и чья неизменность не гарантируется, поэтому наружу отдаётся копия,
 * а в хранилище кладётся копия переданного набора.
 *
 * Запись `null` удаляет ключ из хранилища.
 *
 * @param key ключ в хранилище
 * @param default значение при отсутствии ключа
 * @param commit `true` — синхронная запись через `commit()`, по умолчанию используется `apply()`
 * @return делегат для свойства типа `Set<String>?`
 */
fun SharedPreferences.stringSet(
    key: String,
    default: Set<String>? = null,
    commit: Boolean = false,
): ReadWriteProperty<Any?, Set<String>?> = PreferenceDelegate(
    prefs = this,
    key = key,
    default = default,
    commit = commit,
    // unmodifiableSet, а не toSet(): toSet() возвращает неизменяемый набор только для размеров 0 и 1,
    // для двух и более элементов это обычный LinkedHashSet, который вызывающий код может испортить
    read = { k, d -> getStringSet(k, d?.toMutableSet())?.let { Collections.unmodifiableSet(LinkedHashSet(it)) } },
    write = { k, v -> putStringSet(k, v?.toMutableSet()) },
)

/**
 * Делегат для целочисленного значения.
 *
 * [default] обязателен: его требует сам [SharedPreferences.getInt].
 *
 * @param key ключ в хранилище
 * @param default значение при отсутствии ключа
 * @param commit `true` — синхронная запись через `commit()`, по умолчанию используется `apply()`
 * @return делегат для свойства типа `Int`
 */
fun SharedPreferences.int(
    key: String,
    default: Int,
    commit: Boolean = false,
): ReadWriteProperty<Any?, Int> = PreferenceDelegate(
    prefs = this,
    key = key,
    default = default,
    commit = commit,
    read = { k, d -> getInt(k, d) },
    write = { k, v -> putInt(k, v) },
)

/**
 * Делегат для значения типа `Long`. [default] обязателен — его требует [SharedPreferences.getLong].
 *
 * @param key ключ в хранилище
 * @param default значение при отсутствии ключа
 * @param commit `true` — синхронная запись через `commit()`, по умолчанию используется `apply()`
 * @return делегат для свойства типа `Long`
 */
fun SharedPreferences.long(
    key: String,
    default: Long,
    commit: Boolean = false,
): ReadWriteProperty<Any?, Long> = PreferenceDelegate(
    prefs = this,
    key = key,
    default = default,
    commit = commit,
    read = { k, d -> getLong(k, d) },
    write = { k, v -> putLong(k, v) },
)

/**
 * Делегат для значения типа `Float`. [default] обязателен — его требует [SharedPreferences.getFloat].
 *
 * @param key ключ в хранилище
 * @param default значение при отсутствии ключа
 * @param commit `true` — синхронная запись через `commit()`, по умолчанию используется `apply()`
 * @return делегат для свойства типа `Float`
 */
fun SharedPreferences.float(
    key: String,
    default: Float,
    commit: Boolean = false,
): ReadWriteProperty<Any?, Float> = PreferenceDelegate(
    prefs = this,
    key = key,
    default = default,
    commit = commit,
    read = { k, d -> getFloat(k, d) },
    write = { k, v -> putFloat(k, v) },
)

/**
 * Делегат для значения типа `Boolean`. [default] обязателен — его требует [SharedPreferences.getBoolean].
 *
 * @param key ключ в хранилище
 * @param default значение при отсутствии ключа
 * @param commit `true` — синхронная запись через `commit()`, по умолчанию используется `apply()`
 * @return делегат для свойства типа `Boolean`
 */
fun SharedPreferences.boolean(
    key: String,
    default: Boolean,
    commit: Boolean = false,
): ReadWriteProperty<Any?, Boolean> = PreferenceDelegate(
    prefs = this,
    key = key,
    default = default,
    commit = commit,
    read = { k, d -> getBoolean(k, d) },
    write = { k, v -> putBoolean(k, v) },
)
