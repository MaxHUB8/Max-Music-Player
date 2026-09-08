package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.MusicRepository
import com.example.data.local.UserPreferencesRepository
import com.example.model.Album
import com.example.model.Artist
import com.example.model.EqualizerState
import com.example.model.Folder
import com.example.model.PlaybackState
import com.example.model.Playlist
import com.example.model.Song
import com.example.playback.PlaybackManager
import com.example.ui.theme.MaxThemeStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NavTab {
    HOME,
    LIBRARY,
    PLAYLISTS,
    EQUALIZER,
    SETTINGS
}

enum class LibraryFilter {
    ALL,
    ALBUMS,
    ARTISTS,
    FOLDERS,
    CLOUD_SYNC
}

class MaxViewModel(
    private val repository: MusicRepository,
    private val playbackManager: PlaybackManager,
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    private val _currentTab = MutableStateFlow(NavTab.LIBRARY)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _libraryFilter = MutableStateFlow(LibraryFilter.ALL)
    val libraryFilter: StateFlow<LibraryFilter> = _libraryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isNowPlayingExpanded = MutableStateFlow(false)
    val isNowPlayingExpanded: StateFlow<Boolean> = _isNowPlayingExpanded.asStateFlow()

    private val _isLyricsOpen = MutableStateFlow(false)
    val isLyricsOpen: StateFlow<Boolean> = _isLyricsOpen.asStateFlow()

    private val _isQueueOpen = MutableStateFlow(false)
    val isQueueOpen: StateFlow<Boolean> = _isQueueOpen.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders.asStateFlow()

    private val _themeStyle = MutableStateFlow(MaxThemeStyle.LIQUID_CYBERNETIC)
    val themeStyle: StateFlow<MaxThemeStyle> = _themeStyle.asStateFlow()

    private val _hasStoragePermission = MutableStateFlow(true)
    val hasStoragePermission: StateFlow<Boolean> = _hasStoragePermission.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState
    val equalizerState: StateFlow<EqualizerState> = playbackManager.equalizerState
    val isScanning: StateFlow<Boolean> = repository.isScanning.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val favorites: StateFlow<List<Song>> = repository.favorites.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val playlists: StateFlow<List<Playlist>> = repository.playlists.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val recentHistory: StateFlow<List<com.example.data.local.PlayHistoryEntity>> = repository.recentHistory.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val songs: StateFlow<List<Song>> = combine(repository.songs, _searchQuery) { allSongs, query ->
        if (query.isBlank()) {
            allSongs
        } else {
            allSongs.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadMusic()
        observeTheme()
    }

    fun loadMusic() {
        viewModelScope.launch {
            repository.scanLocalMusic()
            _albums.value = repository.getAlbums()
            _artists.value = repository.getArtists()
            _folders.value = repository.getFolders()
        }
    }

    private fun observeTheme() {
        viewModelScope.launch {
            userPrefs.userSettingsFlow.collect { settings ->
                val style = try {
                    MaxThemeStyle.valueOf(settings.theme)
                } catch (e: Exception) {
                    MaxThemeStyle.LIQUID_CYBERNETIC
                }
                _themeStyle.value = style
            }
        }
    }

    fun setStoragePermission(granted: Boolean) {
        _hasStoragePermission.value = granted
        if (granted) {
            loadMusic()
        }
    }

    fun setTab(tab: NavTab) {
        _currentTab.value = tab
    }

    fun setFilter(filter: LibraryFilter) {
        _libraryFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setNowPlayingExpanded(expanded: Boolean) {
        _isNowPlayingExpanded.value = expanded
    }

    fun setLyricsOpen(open: Boolean) {
        _isLyricsOpen.value = open
    }

    fun setQueueOpen(open: Boolean) {
        _isQueueOpen.value = open
    }

    fun setTheme(theme: MaxThemeStyle) {
        _themeStyle.value = theme
        viewModelScope.launch {
            userPrefs.setTheme(theme.name)
        }
    }

    // Playback Passthrough
    fun playSong(song: Song, queue: List<Song>? = null) {
        playbackManager.playSong(song, queue ?: songs.value)
    }

    fun togglePlayPause() {
        playbackManager.togglePlayPause()
    }

    fun playNext() {
        playbackManager.playNext()
    }

    fun playPrevious() {
        playbackManager.playPrevious()
    }

    fun seekTo(positionMs: Long) {
        playbackManager.seekTo(positionMs)
    }

    fun toggleShuffle() {
        playbackManager.toggleShuffle()
    }

    fun toggleRepeatMode() {
        playbackManager.toggleRepeatMode()
    }

    fun toggleFavoriteCurrent() {
        playbackManager.toggleFavoriteCurrent()
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            repository.toggleFavorite(song)
        }
    }

    fun updateEqualizer(
        isEnabled: Boolean = equalizerState.value.isEnabled,
        presetName: String = equalizerState.value.presetName,
        bandLevels: List<Int> = equalizerState.value.bandLevels,
        bassBoostDb: Int = equalizerState.value.bassBoostDb,
        virtualizerPct: Int = equalizerState.value.virtualizerPct,
        reverbPreset: String = equalizerState.value.reverbPreset,
        isDolbyEnabled: Boolean = equalizerState.value.isDolbyEnabled,
        dolbyProfile: String = equalizerState.value.dolbyProfile,
        dolbySurroundPct: Int = equalizerState.value.dolbySurroundPct,
        dolbyDialogueClarity: Boolean = equalizerState.value.dolbyDialogueClarity,
        dolbyBassDynamic: Boolean = equalizerState.value.dolbyBassDynamic
    ) {
        playbackManager.updateEqualizer(
            isEnabled = isEnabled,
            presetName = presetName,
            bandLevels = bandLevels,
            bassBoostDb = bassBoostDb,
            virtualizerPct = virtualizerPct,
            reverbPreset = reverbPreset,
            isDolbyEnabled = isDolbyEnabled,
            dolbyProfile = dolbyProfile,
            dolbySurroundPct = dolbySurroundPct,
            dolbyDialogueClarity = dolbyDialogueClarity,
            dolbyBassDynamic = dolbyBassDynamic
        )
    }

    fun toggleDolbyAudio(enable: Boolean? = null) {
        playbackManager.toggleDolbyAudio(enable)
    }

    fun setDolbyProfile(profile: String) {
        playbackManager.setDolbyProfile(profile)
    }

    fun setDolbySurroundPct(pct: Int) {
        playbackManager.setDolbySurroundPct(pct)
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            repository.createPlaylist(name)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            repository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun clearRecentHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun playFromHistory(history: com.example.data.local.PlayHistoryEntity) {
        val foundSong = songs.value.find { it.id == history.songId }
        if (foundSong != null) {
            playSong(foundSong)
        } else {
            val fallbackSong = Song(
                id = history.songId,
                title = history.title,
                artist = history.artist,
                album = history.album,
                durationMs = history.durationMs,
                mediaUri = android.net.Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"),
                albumArtUri = history.albumArtUri?.let { android.net.Uri.parse(it) },
                formatBadge = history.formatBadge,
                isDolbyAudio = history.isDolby
            )
            playSong(fallbackSong)
        }
    }

    class Factory(
        private val repository: MusicRepository,
        private val playbackManager: PlaybackManager,
        private val userPrefs: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MaxViewModel(repository, playbackManager, userPrefs) as T
        }
    }
}
