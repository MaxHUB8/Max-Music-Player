package com.example.playback

import android.app.PendingIntent
import android.content.Intent
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.example.MainActivity
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class MaxPlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    // Hardware Audio Effects
    private var hardwareEqualizer: Equalizer? = null
    private var hardwareBassBoost: BassBoost? = null
    private var hardwareVirtualizer: Virtualizer? = null
    private var hardwareReverb: PresetReverb? = null

    companion object {
        var currentInstance: MaxPlaybackService? = null
            private set
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        currentInstance = this

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true) // Handles audio focus automatically
            .setHandleAudioBecomingNoisy(true)        // Pauses on headphone unplug automatically
            .setWakeMode(C.WAKE_MODE_LOCAL)          // WAKELOCK for screen-off playback
            .build()

        val activityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(pendingIntent)
            .setCallback(MediaSessionCallback())
            .build()

        player.addListener(object : Player.Listener {
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                if (audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
                    setupAudioEffects(audioSessionId)
                }
            }
        })
    }

    private fun setupAudioEffects(sessionId: Int) {
        releaseAudioEffects()
        try {
            hardwareEqualizer = Equalizer(0, sessionId).apply { enabled = true }
        } catch (e: Throwable) {
            hardwareEqualizer = null
        }
        try {
            hardwareBassBoost = BassBoost(0, sessionId).apply { enabled = true }
        } catch (e: Throwable) {
            hardwareBassBoost = null
        }
        try {
            hardwareVirtualizer = Virtualizer(0, sessionId).apply { enabled = true }
        } catch (e: Throwable) {
            hardwareVirtualizer = null
        }
        try {
            hardwareReverb = PresetReverb(0, sessionId).apply { enabled = true }
        } catch (e: Throwable) {
            hardwareReverb = null
        }
    }

    fun applyEqualizerSettings(
        isEnabled: Boolean,
        bandLevels: List<Int>, // -12 to +12 dB
        bassBoostDb: Int,
        virtualizerPct: Int,
        reverbPreset: String,
        isDolbyEnabled: Boolean = true,
        dolbyProfile: String = "ATMOS SPATIAL",
        dolbySurroundPct: Int = 85
    ) {
        try {
            // Apply Equalizer with optional Dolby Acoustic Spatial Curve
            hardwareEqualizer?.let { eq ->
                eq.enabled = isEnabled
                val numBands = eq.numberOfBands.toInt()
                val minMb = eq.bandLevelRange[0] // e.g. -1500 mB
                val maxMb = eq.bandLevelRange[1] // e.g. +1500 mB

                // Dolby Acoustic Spatial profiling adjustments:
                // Enhances deep sub-harmonics (31Hz, 62Hz) and 3D air presence (8kHz, 16kHz)
                val dolbyBoostPerBand = if (isDolbyEnabled) {
                    when (dolbyProfile) {
                        "ATMOS SPATIAL" -> listOf(4, 3, 1, 0, 0, 1, 2, 3, 4, 5)
                        "DOLBY CINEMA" -> listOf(5, 4, 2, 0, -1, 1, 2, 4, 5, 6)
                        "DOLBY MUSIC" -> listOf(3, 2, 1, 0, 0, 1, 2, 2, 3, 3)
                        "DOLBY VISION MASTER" -> listOf(4, 3, 1, 0, 1, 2, 3, 4, 5, 5)
                        else -> List(10) { 0 }
                    }
                } else List(10) { 0 }

                for (i in 0 until numBands) {
                    val baseLevelDb = if (i < bandLevels.size) bandLevels[i] else 0
                    val dolbyOffset = if (i < dolbyBoostPerBand.size) dolbyBoostPerBand[i] else 0
                    val combinedDb = (baseLevelDb + dolbyOffset).coerceIn(-12, 12)

                    // map -12..+12 dB to minMb..maxMb
                    val mB = (combinedDb * 100).coerceIn(minMb.toInt(), maxMb.toInt()).toShort()
                    try {
                        eq.setBandLevel(i.toShort(), mB)
                    } catch (ignored: Exception) {}
                }
            }

            // Apply Bass Boost / Dolby Dynamic Sub-Harmonics
            hardwareBassBoost?.let { bb ->
                val effectiveBassDb = if (isDolbyEnabled) {
                    (bassBoostDb + 3).coerceAtMost(19)
                } else bassBoostDb

                bb.enabled = isEnabled && effectiveBassDb > 0
                if (bb.strengthSupported) {
                    val strength = ((effectiveBassDb / 19f) * 1000).toInt().coerceIn(0, 1000).toShort()
                    try {
                        bb.setStrength(strength)
                    } catch (ignored: Exception) {}
                }
            }

            // Apply Virtualizer / Dolby Binaural Spatialization
            hardwareVirtualizer?.let { virt ->
                val effectiveVirtualizerPct = if (isDolbyEnabled) {
                    (virtualizerPct.coerceAtLeast(dolbySurroundPct)).coerceIn(0, 100)
                } else virtualizerPct

                virt.enabled = isEnabled && effectiveVirtualizerPct > 0
                if (virt.strengthSupported) {
                    val strength = ((effectiveVirtualizerPct / 100f) * 1000).toInt().coerceIn(0, 1000).toShort()
                    try {
                        virt.setStrength(strength)
                    } catch (ignored: Exception) {}
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseAudioEffects() {
        try {
            hardwareEqualizer?.release()
        } catch (ignored: Throwable) {}
        try {
            hardwareBassBoost?.release()
        } catch (ignored: Throwable) {}
        try {
            hardwareVirtualizer?.release()
        } catch (ignored: Throwable) {}
        try {
            hardwareReverb?.release()
        } catch (ignored: Throwable) {}

        hardwareEqualizer = null
        hardwareBassBoost = null
        hardwareVirtualizer = null
        hardwareReverb = null
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        currentInstance = null
        releaseAudioEffects()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    fun getPlayer(): ExoPlayer = player

    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }
    }
}
