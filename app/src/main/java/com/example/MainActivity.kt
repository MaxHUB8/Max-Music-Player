package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.ui.MaxViewModel
import com.example.ui.NavTab
import com.example.ui.components.FloatingBottomNav
import com.example.ui.components.MiniPlayerDock
import com.example.ui.screens.EqualizerScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.LyricsSheet
import com.example.ui.screens.PlaylistsScreen
import com.example.ui.screens.QueueSheet
import com.example.ui.screens.NowPlayingSheet
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MaxViewModel by viewModels {
        val app = application as MaxApplication
        MaxViewModel.Factory(app.repository, app.playbackManager, app.userPreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeStyle by viewModel.themeStyle.collectAsState()

            MyApplicationTheme(themeStyle = themeStyle) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MaxViewModel) {
    var hasEnteredSoundstage by remember { mutableStateOf(false) }

    val currentTab by viewModel.currentTab.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val recentHistory by viewModel.recentHistory.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val equalizerState by viewModel.equalizerState.collectAsState()
    val selectedFilter by viewModel.libraryFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val isNowPlayingExpanded by viewModel.isNowPlayingExpanded.collectAsState()
    val isLyricsOpen by viewModel.isLyricsOpen.collectAsState()
    val isQueueOpen by viewModel.isQueueOpen.collectAsState()
    val themeStyle by viewModel.themeStyle.collectAsState()

    // Permission launcher for audio access & notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val audioGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_AUDIO] == true
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        }
        viewModel.setStoragePermission(audioGranted)
    }

    LaunchedEffect(Unit) {
        val perms = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.READ_MEDIA_AUDIO)
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            perms.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(perms.toTypedArray())
    }

    // Intercept Back button when full screens or modal sheets are open
    BackHandler(enabled = isLyricsOpen || isQueueOpen || isNowPlayingExpanded) {
        when {
            isLyricsOpen -> viewModel.setLyricsOpen(false)
            isQueueOpen -> viewModel.setQueueOpen(false)
            isNowPlayingExpanded -> viewModel.setNowPlayingExpanded(false)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        if (!hasEnteredSoundstage) {
            SplashScreen(
                onEnterSoundstage = { hasEnteredSoundstage = true },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Main App Navigation Container
            Box(modifier = Modifier.fillMaxSize()) {
                // Tab Content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    when (currentTab) {
                        NavTab.HOME,
                        NavTab.LIBRARY -> {
                            LibraryScreen(
                                songs = songs,
                                albums = albums,
                                artists = artists,
                                folders = folders,
                                playbackState = playbackState,
                                selectedFilter = selectedFilter,
                                onSelectFilter = { viewModel.setFilter(it) },
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                onSongClick = { viewModel.playSong(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onOpenEqualizer = { viewModel.setTab(NavTab.EQUALIZER) },
                                isScanning = isScanning
                            )
                        }

                        NavTab.PLAYLISTS -> {
                            PlaylistsScreen(
                                playlists = playlists,
                                favorites = favorites,
                                recentHistory = recentHistory,
                                onPlaySong = { song, queue -> viewModel.playSong(song, queue) },
                                onCreatePlaylist = { viewModel.createPlaylist(it) },
                                onDeletePlaylist = { viewModel.deletePlaylist(it) },
                                onPlayHistoryItem = { viewModel.playFromHistory(it) },
                                onClearHistory = { viewModel.clearRecentHistory() },
                                onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) }
                            )
                        }

                        NavTab.EQUALIZER -> {
                            EqualizerScreen(
                                equalizerState = equalizerState,
                                onUpdateEqualizer = {
                                    viewModel.updateEqualizer(
                                        isEnabled = it.isEnabled,
                                        presetName = it.presetName,
                                        bandLevels = it.bandLevels,
                                        bassBoostDb = it.bassBoostDb,
                                        virtualizerPct = it.virtualizerPct,
                                        reverbPreset = it.reverbPreset,
                                        isDolbyEnabled = it.isDolbyEnabled,
                                        dolbyProfile = it.dolbyProfile,
                                        dolbySurroundPct = it.dolbySurroundPct,
                                        dolbyDialogueClarity = it.dolbyDialogueClarity,
                                        dolbyBassDynamic = it.dolbyBassDynamic
                                    )
                                }
                            )
                        }

                        NavTab.SETTINGS -> {
                            SettingsScreen(
                                currentTheme = themeStyle,
                                onSelectTheme = { viewModel.setTheme(it) },
                                onRescanLibrary = { viewModel.loadMusic() }
                            )
                        }
                    }
                }

                // Docked Controls above Bottom Navigation
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                ) {
                    // Mini Player Dock (shown when a track is active)
                    if (playbackState.currentSong != null) {
                        MiniPlayerDock(
                            playbackState = playbackState,
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onSkipNext = { viewModel.playNext() },
                            onOpenQueue = { viewModel.setQueueOpen(true) },
                            onExpandNowPlaying = { viewModel.setNowPlayingExpanded(true) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    // Floating Bottom Navigation
                    FloatingBottomNav(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.setTab(it) },
                        isPlaying = playbackState.isPlaying,
                        onCenterOrbClick = { viewModel.togglePlayPause() }
                    )
                }
            }

            // Expanded Full-Screen Now Playing Sheet
            AnimatedVisibility(
                visible = isNowPlayingExpanded,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NowPlayingSheet(
                    playbackState = playbackState,
                    onCollapse = { viewModel.setNowPlayingExpanded(false) },
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onPrevious = { viewModel.playPrevious() },
                    onNext = { viewModel.playNext() },
                    onSeek = { fraction ->
                        val targetMs = (fraction * playbackState.durationMs).toLong()
                        viewModel.seekTo(targetMs)
                    },
                    onToggleShuffle = { viewModel.toggleShuffle() },
                    onToggleRepeat = { viewModel.toggleRepeatMode() },
                    onToggleFavorite = { viewModel.toggleFavoriteCurrent() },
                    onOpenEqualizer = {
                        viewModel.setNowPlayingExpanded(false)
                        viewModel.setTab(NavTab.EQUALIZER)
                    },
                    onOpenLyrics = { viewModel.setLyricsOpen(true) },
                    onOpenQueue = { viewModel.setQueueOpen(true) }
                )
            }

            // Lyrics Sheet Overlay
            AnimatedVisibility(
                visible = isLyricsOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                LyricsSheet(
                    song = playbackState.currentSong,
                    onClose = { viewModel.setLyricsOpen(false) }
                )
            }

            // Queue Sheet Overlay
            AnimatedVisibility(
                visible = isQueueOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                QueueSheet(
                    playbackState = playbackState,
                    onSongClick = { viewModel.playSong(it) },
                    onClose = { viewModel.setQueueOpen(false) }
                )
            }
        }
    }
}
