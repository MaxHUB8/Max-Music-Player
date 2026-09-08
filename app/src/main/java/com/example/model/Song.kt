package com.example.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val mediaUri: Uri,
    val albumArtUri: Uri? = null,
    val albumId: Long = 0L,
    val formatBadge: String = "FLAC 24-BIT",
    val bitrateBadge: String = "96kHz",
    val isFavorite: Boolean = false,
    val folderName: String = "Music",
    val filePath: String = "",
    val trackNumber: Int = 0,
    val isDolbyAudio: Boolean = false
) {
    val durationFormatted: String
        get() {
            val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val artworkUri: Uri? = null,
    val songCount: Int = 0,
    val year: Int = 2026
)

data class Artist(
    val id: Long,
    val name: String,
    val songCount: Int = 0,
    val albumCount: Int = 0
)

data class Folder(
    val name: String,
    val path: String,
    val songCount: Int
)

data class Playlist(
    val id: Long,
    val name: String,
    val songCount: Int = 0,
    val coverUri: Uri? = null,
    val createdAt: Long = System.currentTimeMillis()
)
