package org.example.prefs

import android.content.SharedPreferences
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class Settings(prefs: SharedPreferences, commit: Boolean = false) {
    var token: String? by prefs.string("auth_token", commit = commit)
    var name: String? by prefs.string("name", default = "guest")
    var tags: Set<String>? by prefs.stringSet("tags")
    var launches: Int by prefs.int("launches", 0)
    var lastSync: Long by prefs.long("last_sync", -1L)
    var ratio: Float by prefs.float("ratio", 1.0f)
    var isPro: Boolean by prefs.boolean("is_pro", false)
}

class PreferenceDelegatesTest {
    @Test
    fun `returns default when key is absent`() {
        val settings = Settings(FakeSharedPreferences())

        assertNull(settings.token)
        assertNull(settings.tags)
        assertEquals("guest", settings.name)
        assertEquals(0, settings.launches)
        assertEquals(-1L, settings.lastSync)
        assertEquals(1.0f, settings.ratio)
        assertFalse(settings.isPro)
    }

    @Test
    fun `writes and reads back every supported type`() {
        val settings = Settings(FakeSharedPreferences())

        settings.token = "abc"
        settings.tags = setOf("a", "b")
        settings.launches = 7
        settings.lastSync = 1_700_000_000_000L
        settings.ratio = 0.5f
        settings.isPro = true

        assertEquals("abc", settings.token)
        assertEquals(setOf("a", "b"), settings.tags)
        assertEquals(7, settings.launches)
        assertEquals(1_700_000_000_000L, settings.lastSync)
        assertEquals(0.5f, settings.ratio)
        assertTrue(settings.isPro)
    }

    @Test
    fun `uses explicit key, not property name`() {
        val prefs = FakeSharedPreferences()
        val settings = Settings(prefs)

        settings.token = "abc"

        assertTrue(prefs.contains("auth_token"))
        assertFalse(prefs.contains("token"))
        assertEquals("abc", prefs.getString("auth_token", null))
    }

    @Test
    fun `properties with different keys do not interfere`() {
        val settings = Settings(FakeSharedPreferences())

        settings.token = "abc"
        settings.name = "leny"

        assertEquals("abc", settings.token)
        assertEquals("leny", settings.name)
    }

    @Test
    fun `reads external changes because value is not cached`() {
        val prefs = FakeSharedPreferences()
        val settings = Settings(prefs)

        settings.launches = 1
        prefs.put("launches", 42)

        assertEquals(42, settings.launches)
    }

    @Test
    fun `writing null removes the key`() {
        val prefs = FakeSharedPreferences()
        val settings = Settings(prefs)

        settings.token = "abc"
        settings.tags = setOf("a")
        assertTrue(prefs.contains("auth_token"))
        assertTrue(prefs.contains("tags"))

        settings.token = null
        settings.tags = null

        assertFalse(prefs.contains("auth_token"))
        assertFalse(prefs.contains("tags"))
        assertNull(settings.token)
    }

    /**
     * Набор берётся из двух элементов намеренно: на одноэлементном `toSet()` тоже вернул бы
     * неизменяемый набор, и проверка проходила бы независимо от реализации делегата.
     */
    @Test
    fun `string set is defensively copied in both directions`() {
        val settings = Settings(FakeSharedPreferences())

        val written = mutableSetOf("a", "b")
        settings.tags = written
        written.add("c")
        assertEquals(setOf("a", "b"), settings.tags)

        @Suppress("UNCHECKED_CAST")
        val read = settings.tags as MutableSet<String>
        assertFailsWith<UnsupportedOperationException> { read.add("d") }
        assertEquals(setOf("a", "b"), settings.tags)
    }

    @Test
    fun `applies by default and commits when asked`() {
        val applying = FakeSharedPreferences()
        Settings(applying).token = "abc"
        assertEquals(1, applying.applyCount)
        assertEquals(0, applying.commitCount)

        val committing = FakeSharedPreferences()
        Settings(committing, commit = true).token = "abc"
        assertEquals(0, committing.applyCount)
        assertEquals(1, committing.commitCount)
    }

    @Test
    fun `same key in different storages stays independent`() {
        val userPrefs = FakeSharedPreferences()
        val cachePrefs = FakeSharedPreferences()

        Settings(userPrefs).token = "user"
        Settings(cachePrefs).token = "cache"

        assertEquals("user", Settings(userPrefs).token)
        assertEquals("cache", Settings(cachePrefs).token)
    }
}
