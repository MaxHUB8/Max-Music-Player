package com.example.playback

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.data.MusicRepository
import com.example.data.local.MaxDao
import com.example.data.local.PlayHistoryEntity
import com.example.data.local.UserPreferencesRepository
import com.example.model.EqualizerState
import com.example.model.PlaybackState
import com.example.model.RepeatMode
import com.example.model.Song
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class PlaybackManager(
    private val context: Context,
    private val repository: MusicRepository,
    private val maxDao: MaxDao,
    private val userPrefs: UserPreferencesRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _equalizerState = MutableStateFlow(EqualizerState())
    val equalizerState: StateFlow<EqualizerState> = _equalizerState.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var progressJob: Job? = null

    init {
        startServiceAndConnect()
        observeSettings()
    }

    private fun startServiceAndConnect() {
        val serviceIntent = Intent(context, MaxPlaybackService::class.java)
        try {
            // Never call startForegroundService for MediaSessionService as it causes ANR/crash
            // if playback is not active immediately. startService or binding is the Android standard.
            context.startService(serviceIntent)
        } catch (ignored: Exception) {}

        try {
            val sessionToken = SessionToken(context, ComponentName(context, MaxPlaybackService::class.java))
            controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture?.addListener({
                try {
                    mediaController = controllerFuture?.get()
                    setupControllerListener()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, MoreExecutors.directExecutor())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observeSettings() {
        scope.launch {
            userPrefs.userSettingsFlow.collect { settings ->
                _playbackState.update {
                    it.copy(
                        isShuffle = settings.isShuffle,
                        repeatMode = when (settings.repeatMode) {
                            "ONE" -> RepeatMode.ONE
                            "OFF" -> RepeatMode.OFF
                            else -> RepeatMode.ALL
                        }
                    )
                }
            }
        }
    }

    private fun setupControllerListener() {
        val controller = mediaController ?: return
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onPlaybackStateChanged(playbackStateInt: Int) {
                if (playbackStateInt == Player.STATE_READY) {
                    _playbackState.update {
                        it.copy(
                            durationMs = controller.duration.coerceAtLeast(0L),
                            bufferedPositionMs = controller.bufferedPosition.coerceAtLeast(0L)
                        )
                    }
                } else if (playbackStateInt == Player.STATE_ENDED) {
                    playNext()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val currentSong = _playbackState.value.queue.getOrNull(_playbackState.value.queueIndex)
                if (currentSong != null) {
                    _playbackState.update {
                        it.copy(
                            currentSong = currentSong,
                            durationMs = currentSong.durationMs,
                            positionMs = 0L
                        )
                    }
                    recordHistory(currentSong)
                }
            }
        })

        // sync initial state
        _playbackState.update {
            it.copy(
                isPlaying = controller.isPlaying,
                positionMs = controller.currentPosition.coerceAtLeast(0L),
                durationMs = controller.duration.coerceAtLeast(0L)
            )
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            var step = 0
            while (isActive) {
                val controller = mediaController
                if (controller != null && controller.isPlaying) {
                    val pos = controller.currentPosition.coerceAtLeast(0L)
                    val dur = controller.duration.coerceAtLeast(0L)
                    val buf = controller.bufferedPosition.coerceAtLeast(0L)

                    // Generate responsive 40-band visualizer spectrum simulation
                    step++
                    val amplitudes = generateVisualizerAmplitudes(step, _equalizerState.value)

                    _playbackState.update {
                        it.copy(
                            positionMs = pos,
                            durationMs = if (dur > 0) dur else it.durationMs,
                            bufferedPositionMs = buf,
                            visualizerAmplitudes = amplitudes
                        )
                    }
                }
                delay(100)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        // Decay visualizer bars gently
        _playbackState.update {
            it.copy(visualizerAmplitudes = List(40) { 0.1f })
        }
    }

    private fun generateVisualizerAmplitudes(step: Int, eq: EqualizerState): List<Float> {
        val list = ArrayList<Float>(40)
        val bassBoostMult = 1f + (eq.bassBoostDb / 20f)
        for (i in 0 until 40) {
            val freqRatio = i / 40f
            // Low frequencies have higher amplitude swing
            val base = if (i < 10) 0.6f * bassBoostMult else 0.4f
            val wave = kotlin.math.sin((step * 0.25f) + (i * 0.4f)) * 0.35f
            val jitter = Random.nextFloat() * 0.2f
            val amp = (base + wave + jitter).coerceIn(0.12f, 1.0f)
            list.add(amp)
        }
        return list
    }

    fun playSong(song: Song, newQueue: List<Song>? = null) {
        val queue = newQueue ?: if (_playbackState.value.queue.isEmpty()) listOf(song) else _playbackState.value.queue
        val index = queue.indexOfFirst { it.id == song.id }.let { if (it == -1) 0 else it }

        _playbackState.update {
            it.copy(
                currentSong = song,
                queue = queue,
                queueIndex = index,
                isPlaying = true,
                positionMs = 0L,
                durationMs = song.durationMs
            )
        }

        val controller = mediaController
        if (controller != null) {
            val metadata = MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .setArtworkUri(song.albumArtUri)
                .build()

            val mediaItem = MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.mediaUri)
                .setMediaMetadata(metadata)
                .build()

            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()
        } else {
            // Fallback directly to service instance if available
            MaxPlaybackService.currentInstance?.getPlayer()?.let { player ->
                val mediaItem = MediaItem.Builder()
                    .setMediaId(song.id.toString())
                    .setUri(song.mediaUri)
                    .build()
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
            }
        }

        startProgressTracker()
        recordHistory(song)
    }

    fun togglePlayPause() {
        val controller = mediaController ?: MaxPlaybackService.currentInstance?.getPlayer()
        if (controller != null) {
            if (controller.isPlaying) {
                controller.pause()
                _playbackState.update { it.copy(isPlaying = false) }
                stopProgressTracker()
            } else {
                controller.play()
                _playbackState.update { it.copy(isPlaying = true) }
                startProgressTracker()
            }
        } else {
            // If no song loaded yet, play first from queue or demo
            val curr = _playbackState.value.currentSong
            if (curr != null) {
                playSong(curr)
            } else {
                val demo = repository.getAudiophileDemoSongs().first()
                playSong(demo, repository.getAudiophileDemoSongs())
            }
        }
    }

    fun playNext() {
        val state = _playbackState.value
        if (state.queue.isEmpty()) return

        val nextIndex = if (state.isShuffle) {
            Random.nextInt(state.queue.size)
        } else {
            (state.queueIndex + 1) % state.queue.size
        }

        val nextSong = state.queue[nextIndex]
        playSong(nextSong, state.queue)
    }

    fun playPrevious() {
        val state = _playbackState.value
        if (state.queue.isEmpty()) return

        // If played more than 3 seconds, restart current track
        if (state.positionMs > 3000) {
            seekTo(0)
            return
        }

        val prevIndex = if (state.queueIndex - 1 < 0) state.queue.size - 1 else state.queueIndex - 1
        val prevSong = state.queue[prevIndex]
        playSong(prevSong, state.queue)
    }

    fun seekTo(positionMs: Long) {
        val controller = mediaController ?: MaxPlaybackService.currentInstance?.getPlayer()
        controller?.seekTo(positionMs)
        _playbackState.update { it.copy(positionMs = positionMs) }
    }

    fun toggleShuffle() {
        val newShuffle = !_playbackState.value.isShuffle
        _playbackState.update { it.copy(isShuffle = newShuffle) }
        scope.launch { userPrefs.setShuffle(newShuffle) }
    }

    fun toggleRepeatMode() {
        val nextMode = when (_playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _playbackState.update { it.copy(repeatMode = nextMode) }
        scope.launch { userPrefs.setRepeatMode(nextMode.name) }
    }

    fun toggleFavoriteCurrent() {
        val song = _playbackState.value.currentSong ?: return
        scope.launch {
            repository.toggleFavorite(song)
            _playbackState.update {
                it.copy(currentSong = song.copy(isFavorite = !song.isFavorite))
            }
        }
    }

    fun updateEqualizer(
        isEnabled: Boolean = _equalizerState.value.isEnabled,
        presetName: String = _equalizerState.value.presetName,
        bandLevels: List<Int> = _equalizerState.value.bandLevels,
        bassBoostDb: Int = _equalizerState.value.bassBoostDb,
        virtualizerPct: Int = _equalizerState.value.virtualizerPct,
        reverbPreset: String = _equalizerState.value.reverbPreset,
        isDolbyEnabled: Boolean = _equalizerState.value.isDolbyEnabled,
        dolbyProfile: String = _equalizerState.value.dolbyProfile,
        dolbySurroundPct: Int = _equalizerState.value.dolbySurroundPct,
        dolbyDialogueClarity: Boolean = _equalizerState.value.dolbyDialogueClarity,
        dolbyBassDynamic: Boolean = _equalizerState.value.dolbyBassDynamic
    ) {
        val newState = EqualizerState(
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
        _equalizerState.value = newState

        MaxPlaybackService.currentInstance?.applyEqualizerSettings(
            isEnabled = isEnabled,
            bandLevels = bandLevels,
            bassBoostDb = bassBoostDb,
            virtualizerPct = virtualizerPct,
            reverbPreset = reverbPreset,
            isDolbyEnabled = isDolbyEnabled,
            dolbyProfile = dolbyProfile,
            dolbySurroundPct = dolbySurroundPct
        )
    }

    fun toggleDolbyAudio(enable: Boolean? = null) {
        val newDolby = enable ?: !_equalizerState.value.isDolbyEnabled
        updateEqualizer(isDolbyEnabled = newDolby)
    }

    fun setDolbyProfile(profile: String) {
        updateEqualizer(dolbyProfile = profile, isDolbyEnabled = true)
    }

    fun setDolbySurroundPct(pct: Int) {
        updateEqualizer(dolbySurroundPct = pct.coerceIn(0, 100))
    }

    private fun recordHistory(song: Song) {
        scope.launch(Dispatchers.IO) {
            try {
                maxDao.insertHistory(
                    PlayHistoryEntity(
                        songId = song.id,
                        title = song.title,
                        artist = song.artist,
                        album = song.album,
                        durationMs = song.durationMs,
                        albumArtUri = song.albumArtUri?.toString(),
                        playedAt = System.currentTimeMillis(),
                        positionMs = 0L,
                        formatBadge = song.formatBadge,
                        isDolby = song.isDolbyAudio || _equalizerState.value.isDolbyEnabled
                    )
                )
                userPrefs.savePlaybackState(song.id, 0L)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
