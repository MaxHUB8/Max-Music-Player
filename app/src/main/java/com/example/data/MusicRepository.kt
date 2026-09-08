package com.example.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.data.local.FavoriteEntity
import com.example.data.local.MaxDao
import com.example.data.local.PlayHistoryEntity
import com.example.data.local.PlaylistEntity
import com.example.data.local.PlaylistSongCrossRef
import com.example.model.Album
import com.example.model.Artist
import com.example.model.Folder
import com.example.model.Playlist
import com.example.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

class MusicRepository(
    private val context: Context,
    private val maxDao: MaxDao
) {
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: Flow<List<Song>> = _songs.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: Flow<Boolean> = _isScanning.asStateFlow()

    val favorites: Flow<List<Song>> = maxDao.getAllFavorites().map { entities ->
        entities.map { entity ->
            val mediaUri = entity.mediaUri?.let { Uri.parse(it) }
                ?: ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, entity.songId)
            Song(
                id = entity.songId,
                title = entity.title,
                artist = entity.artist,
                album = entity.album,
                durationMs = entity.durationMs,
                mediaUri = mediaUri,
                albumArtUri = entity.albumArtUri?.let { Uri.parse(it) },
                isFavorite = true,
                isDolbyAudio = entity.isDolby,
                formatBadge = if (entity.isDolby) "DOLBY ATMOS" else "FLAC 24-BIT"
            )
        }
    }

    val playlists: Flow<List<Playlist>> = maxDao.getAllPlaylists().map { entities ->
        entities.map {
            Playlist(
                id = it.id,
                name = it.name,
                coverUri = it.coverUri?.let { uriStr -> Uri.parse(uriStr) },
                createdAt = it.createdAt
            )
        }
    }

    val recentHistory: Flow<List<PlayHistoryEntity>> = maxDao.getRecentHistory(50)

    suspend fun scanLocalMusic(): List<Song> = withContext(Dispatchers.IO) {
        _isScanning.value = true
        val songList = mutableListOf<Song>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TRACK
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 5000"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        try {
            // Fetch all favorite song IDs in a single indexed query to eliminate N+1 DB lookups and ANR
            val favoriteIds = try {
                maxDao.getAllFavoriteSongIds().toSet()
            } catch (e: Exception) {
                emptySet()
            }

            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )

            cursor?.use {
                val idCol = it.getColumnIndex(MediaStore.Audio.Media._ID)
                val titleCol = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val artistCol = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val albumCol = it.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                val durationCol = it.getColumnIndex(MediaStore.Audio.Media.DURATION)
                val albumIdCol = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                val dataCol = it.getColumnIndex(MediaStore.Audio.Media.DATA)
                val trackCol = it.getColumnIndex(MediaStore.Audio.Media.TRACK)

                while (it.moveToNext()) {
                    val id = if (idCol >= 0) it.getLong(idCol) else 0L
                    val title = if (titleCol >= 0) it.getString(titleCol) ?: "Unknown Track" else "Unknown Track"
                    val artist = if (artistCol >= 0) it.getString(artistCol) ?: "Unknown Artist" else "Unknown Artist"
                    val album = if (albumCol >= 0) it.getString(albumCol) ?: "Unknown Album" else "Unknown Album"
                    val durationMs = if (durationCol >= 0) it.getLong(durationCol) else 0L
                    val albumId = if (albumIdCol >= 0) it.getLong(albumIdCol) else 0L
                    val path = if (dataCol >= 0) it.getString(dataCol) ?: "" else ""
                    val trackNum = if (trackCol >= 0) it.getInt(trackCol) else 0

                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val albumArtUri = Uri.parse("content://media/external/audio/albumart/$albumId")

                    val ext = if (path.isNotEmpty()) File(path).extension.uppercase() else "MP3"
                    val isDolbyAudio = title.contains("dolby", ignoreCase = true) ||
                            title.contains("atmos", ignoreCase = true) ||
                            path.contains("atmos", ignoreCase = true) ||
                            path.contains("dolby", ignoreCase = true)

                    val formatBadge = when {
                        isDolbyAudio -> "DOLBY ATMOS"
                        ext == "FLAC" -> "FLAC 24-BIT"
                        ext == "WAV" -> "WAV 96kHz"
                        ext == "OGG" -> "OGG VORBIS"
                        ext in listOf("M4A", "AAC") -> "AAC 320K"
                        ext == "OPUS" -> "OPUS 48kHz"
                        else -> "MP3 320K"
                    }

                    val bitrateBadge = when {
                        isDolbyAudio -> "Spatial 3D"
                        formatBadge.contains("FLAC") -> "Lossless"
                        else -> "320 KBPS"
                    }

                    val folderName = if (path.isNotEmpty()) {
                        File(path).parentFile?.name ?: "Music"
                    } else "Music"

                    val isFav = favoriteIds.contains(id)

                    songList.add(
                        Song(
                            id = id,
                            title = title,
                            artist = if (artist.contains("<unknown>")) "Unknown Artist" else artist,
                            album = if (album.contains("<unknown>")) "Unknown Album" else album,
                            durationMs = durationMs,
                            mediaUri = contentUri,
                            albumArtUri = albumArtUri,
                            albumId = albumId,
                            formatBadge = formatBadge,
                            bitrateBadge = bitrateBadge,
                            isFavorite = isFav,
                            folderName = folderName,
                            filePath = path,
                            trackNumber = trackNum,
                            isDolbyAudio = isDolbyAudio
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // If no local files were found on device/emulator, provide the authentic MAX Audiophile Sessions
        if (songList.isEmpty()) {
            songList.addAll(getAudiophileDemoSongs())
        }

        _songs.value = songList
        _isScanning.value = false
        songList
    }

    suspend fun toggleFavorite(song: Song) = withContext(Dispatchers.IO) {
        val isFav = maxDao.isFavorite(song.id)
        if (isFav) {
            maxDao.deleteFavorite(song.id)
        } else {
            maxDao.insertFavorite(
                FavoriteEntity(
                    songId = song.id,
                    title = song.title,
                    artist = song.artist,
                    album = song.album,
                    durationMs = song.durationMs,
                    albumArtUri = song.albumArtUri?.toString(),
                    mediaUri = song.mediaUri.toString(),
                    isDolby = song.isDolbyAudio
                )
            )
        }
        // update memory state
        val updated = _songs.value.map {
            if (it.id == song.id) it.copy(isFavorite = !isFav) else it
        }
        _songs.value = updated
    }

    suspend fun createPlaylist(name: String): Long = withContext(Dispatchers.IO) {
        maxDao.insertPlaylist(PlaylistEntity(name = name))
    }

    suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        maxDao.deletePlaylist(playlistId)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        maxDao.addSongToPlaylist(PlaylistSongCrossRef(playlistId = playlistId, songId = songId))
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        maxDao.removeSongFromPlaylist(playlistId = playlistId, songId = songId)
    }

    fun getSongsForPlaylist(playlistId: Long): Flow<List<Song>> {
        return combine(maxDao.getSongIdsForPlaylist(playlistId), songs) { songIds, allSongs ->
            val songMap = allSongs.associateBy { it.id }
            songIds.mapNotNull { songMap[it] }
        }
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        maxDao.clearHistory()
    }

    suspend fun deleteHistoryItem(id: Long) = withContext(Dispatchers.IO) {
        maxDao.deleteHistoryItem(id)
    }

    suspend fun getAlbums(): List<Album> = withContext(Dispatchers.Default) {
        _songs.value.groupBy { it.albumId }.map { (_, albumSongs) ->
            val first = albumSongs.first()
            Album(
                id = first.albumId,
                title = first.album,
                artist = first.artist,
                artworkUri = first.albumArtUri,
                songCount = albumSongs.size
            )
        }
    }

    suspend fun getArtists(): List<Artist> = withContext(Dispatchers.Default) {
        _songs.value.groupBy { it.artist }.entries.toList().mapIndexed { index, entry ->
            val artistName = entry.key
            val artistSongs = entry.value
            val albums = artistSongs.map { it.album }.distinct().size
            Artist(
                id = index.toLong(),
                name = artistName,
                songCount = artistSongs.size,
                albumCount = albums
            )
        }
    }

    suspend fun getFolders(): List<Folder> = withContext(Dispatchers.Default) {
        _songs.value.groupBy { it.folderName }.map { (folderName, folderSongs) ->
            Folder(
                name = folderName,
                path = folderSongs.firstOrNull()?.filePath ?: "",
                songCount = folderSongs.size
            )
        }
    }

    fun getAudiophileDemoSongs(): List<Song> {
        return listOf(
            Song(
                id = 1001L,
                title = "NEON HORIZON",
                artist = "Kavinsky",
                album = "High Voltage Edition",
                durationMs = 252000L, // 04:12
                mediaUri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"),
                albumArtUri = Uri.parse("https://lh3.googleusercontent.com/aida-public/AB6AXuDx5pKHh6htJiLRK5XwjRjvim3v9pG_7jh6jQa5Gp9NpU8LivSwiP4WFPsLzEg0u0UJJNoI87DNCiooZn8U-hRvn55AEOwGHrHf-lKmd83rXj53-tw5RsO8h_JRlMT7-FQWW6pA6f6CTO5FsbLp_S7y8dxjhFQ_UIp2wuW16J0po6wqo-riomj2S9WPBaNWYpGegeOVSitbOZ5iApKKM4ySaKH_akgY23MSkRMqc7SfGP5NEH-ApS8n"),
                formatBadge = "FLAC 24-BIT",
                bitrateBadge = "96kHz",
                isFavorite = true,
                folderName = "Hi-Res Master"
            ),
            Song(
                id = 1002L,
                title = "CYBERPUNK CITY - SYNTHWAVE VIBES",
                artist = "Synthwave Collective",
                album = "Neon Dreams 2026",
                durationMs = 228000L, // 03:48
                mediaUri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3"),
                albumArtUri = Uri.parse("https://lh3.googleusercontent.com/aida-public/AB6AXuDFc-_fbceIML90pRdRdtODzR8HD5CR1EOrPNs12xgYA-ySJ8qPuBuKOgHXO3rTJnCkLX3lzawdwlFDhasATIcKTd3cZN-SDc8TgVsBsRCUR8v4VuYmGR1BbCUhA9aJAQfX81nPvQYjmnMD-kJyg6IhvIYnhM-Lkw06S9wsPmw7wXudDQOS2pu6Sce_jhqek28X_hIs026VR0frgEa9yzh8F2eJK74u9tvuuAX0IPbiXthR9NJPA_Ha"),
                formatBadge = "320 KBPS",
                bitrateBadge = "MP3",
                isFavorite = false,
                folderName = "Synthwave"
            ),
            Song(
                id = 1003L,
                title = "LIQUID PROTOCOL",
                artist = "Savant & Max",
                album = "Quantum Fluid",
                durationMs = 304000L, // 05:04
                mediaUri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv"),
                albumArtUri = Uri.parse("https://lh3.googleusercontent.com/aida-public/AB6AXuBdNQtqpY1uZW7MXb2SL2Bv1LnFgC8_kE1P0fntObGDqFim4vO2pw4EfqwcY_nT9pwAi9wBcMkzSJapfJc_VeUzoXUrHVoJYEfUuVTHIUz6HGBb6MxEk7rr6gzxgSSklmMKrFYtKu2FwgjI20JNwIf6urZqXeKxiDPDN3bWLdLxDqnN0J71_vk_QB0srteFsZvRavgvTHtaiawdFVAloPn2jvlig-oMzzNHRTVerDx8JZyBbEaY6JMP"),
                formatBadge = "FLAC LOSSLESS",
                bitrateBadge = "Spatial 3D",
                isFavorite = true,
                folderName = "Electronic"
            ),
            Song(
                id = 1004L,
                title = "HYPERSPACE DRIFT",
                artist = "Starlight Orbit",
                album = "Cosmic Dimension",
                durationMs = 276000L, // 04:36
                mediaUri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"),
                albumArtUri = Uri.parse("https://lh3.googleusercontent.com/aida-public/AB6AXuBdPwZ-ya-Xr3otms9ZdaTVeNIVmYQvugd3UzeyenIUw-f9Uo_QoY41DF6vT0KJPLUGXrVidH1bgwMeLmXaXrps6DKGyfxzZkyp_mZ3rCPhkTNUjWmJenMWgdHMhywswRou1yCP7ZR0ReW4PNunbYa7GjkJZoGoozzoEAgIg-wP-Ei08fJEvF-Ds0oxPXvf2Xyw62c0z76N9VP61VF-TsI4UXLmUNB7WvJxmMspUI4bx7xaMO6fj4h8"),
                formatBadge = "FLAC 24/96",
                bitrateBadge = "Hi-Res Audio",
                isFavorite = false,
                folderName = "Hi-Res Master"
            ),
            Song(
                id = 1005L,
                title = "CHRONO TRIGGER [NEON REMIX]",
                artist = "CYBER HORIZON ft. ASTRO",
                album = "Time's Scars 2026",
                durationMs = 252000L, // 04:12
                mediaUri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3"),
                albumArtUri = Uri.parse("https://lh3.googleusercontent.com/aida-public/AB6AXuCv44IPyD3pqOFEtSxZAoUOuTCwt95qUN0XRDDZMsmTNx7elYxWx0kap_wAeS8p1jR6qDdZD0U_Ix0evPNLDL3UA2PKNmXjR2dPbHhPGekPpFZivilxid7HPKtu_SBUf8cK7HuXQ7lHPlMJBpsQQyWYrHKBGXF4ZYOC8EUxN0Ak0YZR2XKit9cnHywVXg9Z_fywjSvnop2Nc39p6R2J4Nt-nmlU053UYEBNbUZlHG-uTME253dXDLGr"),
                formatBadge = "LOSSLESS 24-BIT",
                bitrateBadge = "96kHz FLAC",
                isFavorite = true,
                folderName = "Hi-Res Master"
            ),
            Song(
                id = 1006L,
                title = "CHRONO RESONANCE",
                artist = "Kavinsky & Daft Club",
                album = "Discovery Remaster",
                durationMs = 199000L, // 03:19
                mediaUri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"),
                albumArtUri = Uri.parse("https://lh3.googleusercontent.com/aida-public/AB6AXuDTR5agDlS1Nw_H_cahAbuhA04nTCogSvmVTr6U9YfetJCYlAwqvLFz-s8hRx0CUyrkOI-_mIB-1e3vazR022FZYVCPYMu3KCuwEGrZ0Nl9R9jIMToFWWnMeksgVraTH-4rRirQD5wk8JO6zGiRwxBoHbVvSiIyUK0nvubl1sWjYj8g7ROvulWboiLhpubZPyH4Lvspk50w5zRTkapryNPNQMdxkGmWKaXbWtRhEvX8B89VmQV5D0zu"),
                formatBadge = "320 KBPS",
                bitrateBadge = "LDAC 990K",
                isFavorite = false,
                folderName = "Synthwave"
            )
        )
    }
}
