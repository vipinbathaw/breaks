package com.dhokla.breaks.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.MutablePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

enum class ReminderStyle {
    FULL_SCREEN,
    NOTIFICATION
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    fun next(): ThemeMode = entries[(ordinal + 1) % entries.size]
}

data class BreaksPrefs(
    val onboarded: Boolean,
    val intervalMinutes: Int,
    val style: ReminderStyle,
    val soundEnabled: Boolean,
    val nextBreakAt: Long,
    val themeMode: ThemeMode
)

private val Context.dataStore by preferencesDataStore(name = "breaks")

class BreaksStore(private val context: Context) {

    private object Keys {
        val ONBOARDED = booleanPreferencesKey("onboarded")
        val INTERVAL_MINUTES = intPreferencesKey("interval_minutes")
        val STYLE = stringPreferencesKey("reminder_style")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val NEXT_BREAK_AT = longPreferencesKey("next_break_at")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val prefs: Flow<BreaksPrefs> = context.dataStore.data.map { p ->
        BreaksPrefs(
            onboarded = p[Keys.ONBOARDED] ?: false,
            intervalMinutes = p[Keys.INTERVAL_MINUTES] ?: DEFAULT_INTERVAL_MINUTES,
            style = p[Keys.STYLE]?.let { s ->
                ReminderStyle.entries.firstOrNull { it.name == s }
            } ?: ReminderStyle.FULL_SCREEN,
            soundEnabled = p[Keys.SOUND_ENABLED] ?: true,
            nextBreakAt = p[Keys.NEXT_BREAK_AT] ?: 0L,
            themeMode = p[Keys.THEME_MODE]?.let { s ->
                ThemeMode.entries.firstOrNull { it.name == s }
            } ?: ThemeMode.SYSTEM
        )
    }

    suspend fun snapshot(): BreaksPrefs = prefs.first()

    suspend fun setOnboarded() = edit { it[Keys.ONBOARDED] = true }

    suspend fun setInterval(minutes: Int) = edit { it[Keys.INTERVAL_MINUTES] = minutes }

    suspend fun setStyle(style: ReminderStyle) = edit { it[Keys.STYLE] = style.name }

    suspend fun setSoundEnabled(enabled: Boolean) = edit { it[Keys.SOUND_ENABLED] = enabled }

    suspend fun setThemeMode(mode: ThemeMode) = edit { it[Keys.THEME_MODE] = mode.name }

    suspend fun setNextBreakAt(atMs: Long) = edit { it[Keys.NEXT_BREAK_AT] = atMs }

    private suspend fun edit(transform: (MutablePreferences) -> Unit) {
        context.dataStore.edit(transform)
    }

    companion object {
        const val DEFAULT_INTERVAL_MINUTES = 20
        val INTERVAL_PRESETS = listOf(15, 20, 30, 45, 60)
    }
}
