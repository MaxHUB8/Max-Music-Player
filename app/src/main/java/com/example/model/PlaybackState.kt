package com.example.model

data class EqualizerState(
    val isEnabled: Boolean = true,
    val presetName: String = "Cyber Bass",
    // 10-band frequencies: 31Hz, 62Hz, 125Hz, 250Hz, 500Hz, 1kHz, 2kHz, 4kHz, 8kHz, 16kHz (-12 dB to +12 dB)
    val bandLevels: List<Int> = listOf(6, 8, 4, 2, -1, 0, 3, 5, 7, 9),
    val bassBoostDb: Int = 8, // 0 to 19 dB
    val virtualizerPct: Int = 85, // 0 to 100%
    val reverbPreset: String = "Club Glass", // Off, Small Room, Medium Room, Large Room, Medium Hall, Large Hall, Plate, Club Glass
    val isDolbyEnabled: Boolean = true,
    val dolbyProfile: String = "ATMOS SPATIAL", // OFF, ATMOS SPATIAL, DOLBY CINEMA, DOLBY MUSIC, DOLBY VISION MASTER
    val dolbySurroundPct: Int = 85, // 0 to 100%
    val dolbyDialogueClarity: Boolean = true,
    val dolbyBassDynamic: Boolean = true
)

enum class RepeatMode {
    OFF, ALL, ONE
}

data class PlaybackState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val isShuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.ALL,
    val queue: List<Song> = emptyList(),
    val queueIndex: Int = 0,
    val audioSessionId: Int = 0,
    val visualizerAmplitudes: List<Float> = List(40) { 0.2f }
) {
    val progress: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    val positionFormatted: String
        get() {
            val totalSeconds = (positionMs / 1000).coerceAtLeast(0)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

    val remainingFormatted: String
        get() {
            val remainingSec = ((durationMs - positionMs) / 1000).coerceAtLeast(0)
            val minutes = remainingSec / 60
            val seconds = remainingSec % 60
            return String.format("-%02d:%02d", minutes, seconds)
        }
}
