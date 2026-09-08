package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    indices = [
        Index(value = ["addedAt"])
    ]
)
data class FavoriteEntity(
    @PrimaryKey val songId: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val albumArtUri: String?,
    val addedAt: Long = System.currentTimeMillis(),
    val mediaUri: String? = null,
    val isDolby: Boolean = false
)

@Entity(
    tableName = "playlists",
    indices = [
        Index(value = ["createdAt"])
    ]
)
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val coverUri: String? = null
)

@Entity(
    tableName = "playlist_songs",
    primaryKeys = ["playlistId", "songId"],
    indices = [
        Index(value = ["playlistId"]),
        Index(value = ["songId"])
    ]
)
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long,
    val orderIndex: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "play_history",
    indices = [
        Index(value = ["playedAt"]),
        Index(value = ["songId"])
    ]
)
data class PlayHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val songId: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long = 0L,
    val albumArtUri: String?,
    val playedAt: Long = System.currentTimeMillis(),
    val positionMs: Long = 0L,
    val formatBadge: String = "LOSSLESS",
    val isDolby: Boolean = false
)

@Entity(
    tableName = "equalizer_presets"
)
data class EqualizerPresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val bandLevelsCsv: String, // comma-separated 10 bands
    val bassBoostDb: Int = 0,
    val virtualizerPct: Int = 0,
    val reverbPreset: String = "Club Glass",
    val isCustom: Boolean = false,
    val isDolbyEnabled: Boolean = false,
    val dolbyProfile: String = "OFF",
    val dolbySurroundPct: Int = 50
)

