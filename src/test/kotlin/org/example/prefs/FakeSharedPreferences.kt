package org.example.prefs

import android.content.SharedPreferences

/**
 * Реализация [SharedPreferences] в памяти для тестов: методы настоящего android.jar-стаба
 * кидают `RuntimeException("Stub!")`, поэтому пользоваться им напрямую нельзя.
 *
 * Намеренно обычный класс, а не `data class`: экземпляр представляет отдельный файл префов и должен
 * сравниваться по identity, как настоящий `SharedPreferencesImpl`, — два разных хранилища с одинаковым
 * содержимым не одно и то же.
 */
class FakeSharedPreferences(initial: Map<String, Any> = emptyMap()) : SharedPreferences {
    private val values: MutableMap<String, Any> = initial.toMutableMap()

    var applyCount: Int = 0
        private set
    var commitCount: Int = 0
        private set

    /** Прямая запись в обход делегата — для проверки, что значения не кэшируются. */
    fun put(key: String, value: Any) {
        values[key] = value
    }

    override fun getAll(): MutableMap<String, *> = values.toMutableMap()

    override fun getString(key: String, defValue: String?): String? = values[key]?.let { it as String } ?: defValue

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? =
        values[key]?.let { (it as Set<String>).toMutableSet() } ?: defValues

    override fun getInt(key: String, defValue: Int): Int = values[key]?.let { it as Int } ?: defValue

    override fun getLong(key: String, defValue: Long): Long = values[key]?.let { it as Long } ?: defValue

    override fun getFloat(key: String, defValue: Float): Float = values[key]?.let { it as Float } ?: defValue

    override fun getBoolean(key: String, defValue: Boolean): Boolean = values[key]?.let { it as Boolean } ?: defValue

    override fun contains(key: String): Boolean = key in values

    override fun edit(): SharedPreferences.Editor = FakeEditor()

    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener,
    ): Unit = TODO("не используется в тестах делегатов")

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener,
    ): Unit = TODO("не используется в тестах делегатов")

    /** Накапливает изменения до `apply()`/`commit()`; `null` в [pending] означает удаление ключа. */
    private inner class FakeEditor : SharedPreferences.Editor {
        private val pending = mutableMapOf<String, Any?>()
        private var clearRequested = false

        override fun putString(key: String, value: String?): SharedPreferences.Editor = set(key, value)

        override fun putStringSet(key: String, values: MutableSet<String>?): SharedPreferences.Editor =
            set(key, values?.toSet())

        override fun putInt(key: String, value: Int): SharedPreferences.Editor = set(key, value)

        override fun putLong(key: String, value: Long): SharedPreferences.Editor = set(key, value)

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor = set(key, value)

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor = set(key, value)

        override fun remove(key: String): SharedPreferences.Editor = set(key, null)

        override fun clear(): SharedPreferences.Editor = apply { clearRequested = true }

        override fun apply() {
            flush()
            applyCount++
        }

        override fun commit(): Boolean {
            flush()
            commitCount++
            return true
        }

        private fun set(key: String, value: Any?): SharedPreferences.Editor = apply { pending[key] = value }

        private fun flush() {
            if (clearRequested) values.clear()
            for ((key, value) in pending) {
                if (value == null) values.remove(key) else values[key] = value
            }
            pending.clear()
            clearRequested = false
        }
    }
}
