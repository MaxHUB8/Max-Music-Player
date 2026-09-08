package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "max_user_settings")

data class UserSettings(
    val theme: String = "LIQUID_CYBERNETIC",
    val lastSongId: Long = -1L,
    val lastPositionMs: Long = 0L,
    val isShuffle: Boolean = false,
    val repeatMode: String = "ALL",
    val gaplessPlayback: Boolean = true,
    val crossfadeSeconds: Int = 0,
    val highResOutput: Boolean = true,
    val minDurationSec: Int = 10,
    val bluetoothAutoResume: Boolean = true
)

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val KEY_THEME = stringPreferencesKey("theme_style")
        val KEY_LAST_SONG_ID = longPreferencesKey("last_song_id")
        val KEY_LAST_POSITION = longPreferencesKey("last_position_ms")
        val KEY_SHUFFLE = booleanPreferencesKey("is_shuffle")
        val KEY_REPEAT = stringPreferencesKey("repeat_mode")
        val KEY_GAPLESS = booleanPreferencesKey("gapless_playback")
        val KEY_CROSSFADE = intPreferencesKey("crossfade_sec")
        val KEY_HIGH_RES = booleanPreferencesKey("high_res_output")
        val KEY_MIN_DURATION = intPreferencesKey("min_duration_sec")
        val KEY_BT_RESUME = booleanPreferencesKey("bt_auto_resume")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            theme = prefs[KEY_THEME] ?: "LIQUID_CYBERNETIC",
            lastSongId = prefs[KEY_LAST_SONG_ID] ?: -1L,
            lastPositionMs = prefs[KEY_LAST_POSITION] ?: 0L,
            isShuffle = prefs[KEY_SHUFFLE] ?: false,
            repeatMode = prefs[KEY_REPEAT] ?: "ALL",
            gaplessPlayback = prefs[KEY_GAPLESS] ?: true,
            crossfadeSeconds = prefs[KEY_CROSSFADE] ?: 0,
            highResOutput = prefs[KEY_HIGH_RES] ?: true,
            minDurationSec = prefs[KEY_MIN_DURATION] ?: 10,
            bluetoothAutoResume = prefs[KEY_BT_RESUME] ?: true
        )
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { it[KEY_THEME] = theme }
    }

    suspend fun savePlaybackState(songId: Long, positionMs: Long) {
        context.dataStore.edit {
            it[KEY_LAST_SONG_ID] = songId
            it[KEY_LAST_POSITION] = positionMs
        }
    }

    suspend fun setShuffle(shuffle: Boolean) {
        context.dataStore.edit { it[KEY_SHUFFLE] = shuffle }
    }

    suspend fun setRepeatMode(mode: String) {
        context.dataStore.edit { it[KEY_REPEAT] = mode }
    }

    suspend fun setGapless(enabled: Boolean) {
        context.dataStore.edit { it[KEY_GAPLESS] = enabled }
    }

    suspend fun setHighRes(enabled: Boolean) {
        context.dataStore.edit { it[KEY_HIGH_RES] = enabled }
    }

    suspend fun setCrossfade(seconds: Int) {
        context.dataStore.edit { it[KEY_CROSSFADE] = seconds }
    }
}
