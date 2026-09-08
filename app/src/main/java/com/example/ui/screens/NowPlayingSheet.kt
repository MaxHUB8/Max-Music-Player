package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.PlaybackState
import com.example.model.RepeatMode
import com.example.ui.components.AudioBadge
import com.example.ui.components.AudioVisualizerWave
import com.example.ui.components.LiquidSlider
import com.example.ui.components.NeonOrb
import com.example.ui.components.liquidGlass
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyberMint
import com.example.ui.theme.MonospaceFontFamily
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleLight
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.SurfaceContainerLow

@Composable
fun NowPlayingSheet(
    playbackState: PlaybackState,
    onCollapse: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenEqualizer: () -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val song = playbackState.currentSong ?: return

    Box(
        modifier = modifier
            .testTag("now_playing_sheet")
            .fillMaxSize()
            .background(AmoledBlack)
            .drawBehind {
                // Dynamic ambient lighting behind artwork
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonPurple.copy(alpha = 0.28f), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.35f),
                        radius = size.width * 0.65f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonBlue.copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.45f),
                        radius = size.width * 0.55f
                    )
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Navigation Console
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onCollapse,
                    modifier = Modifier
                        .testTag("now_playing_collapse_button")
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse Now Playing",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Lossless badge pill
                AudioBadge(
                    text = "LOSSLESS 24-BIT / 96kHz FLAC",
                    color = CyberMint
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onOpenEqualizer,
                        modifier = Modifier
                            .testTag("now_playing_eq_button")
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerLow)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Open Equalizer",
                            tint = NeonPurpleLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { /* Device routing */ },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerLow)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cast,
                            contentDescription = "Cast audio",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 2. Large Squircle Album Art with Ambient Halo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .aspectRatio(1f)
                        .shadow(
                            elevation = 32.dp,
                            shape = RoundedCornerShape(32.dp),
                            ambientColor = NeonPurple,
                            spotColor = NeonBlue
                        )
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF101018))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    NeonPurple.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                ) {
                    if (song.albumArtUri != null) {
                        AsyncImage(
                            model = song.albumArtUri,
                            contentDescription = song.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = NeonPurple,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }

                    // Floating format badge on bottom-left corner of artwork
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AudioBadge(
                            text = song.formatBadge,
                            color = CyberMint,
                            icon = Icons.Default.SurroundSound
                        )
                        if (song.isDolbyAudio) {
                            AudioBadge(
                                text = "DOLBY ATMOS™",
                                color = NeonPurpleLight,
                                icon = Icons.Default.SurroundSound
                            )
                        }
                    }
                }
            }

            // 3. Track Metadata + Favorite
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = song.artist,
                            color = OnSurfaceVariant,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Artist",
                            tint = CyberMint,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .testTag("now_playing_fav_toggle")
                        .size(46.dp)
                ) {
                    Icon(
                        imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle favorite",
                        tint = if (song.isFavorite) CyberMint else OnSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // 4. Live 40-Bar Organic Reactive Equalizer Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                AudioVisualizerWave(
                    amplitudes = playbackState.visualizerAmplitudes,
                    height = 36.dp,
                    barCount = 40
                )
            }

            // 5. Scrubber with Timestamps
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                LiquidSlider(
                    progress = playbackState.progress,
                    onSeek = { fraction ->
                        val targetMs = (fraction * playbackState.durationMs).toLong()
                        onSeek(fraction)
                    },
                    testTag = "now_playing_slider"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = playbackState.positionFormatted,
                        color = CyberMint,
                        fontFamily = MonospaceFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = playbackState.remainingFormatted,
                        color = OnSurfaceVariant.copy(alpha = 0.7f),
                        fontFamily = MonospaceFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 6. Glass Control Island
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        shape = RoundedCornerShape(32.dp),
                        backgroundColor = Color(0x1F181726),
                        borderColor = Color.White.copy(alpha = 0.18f),
                        glowColor = NeonPurple.copy(alpha = 0.2f),
                        elevation = 16.dp
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shuffle
                    IconButton(
                        onClick = onToggleShuffle,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (playbackState.isShuffle) CyberMint else OnSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Previous
                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier
                            .testTag("now_playing_prev_button")
                            .size(46.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Track",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Floating Neon Play/Pause Orb
                    NeonOrb(
                        isPlaying = playbackState.isPlaying,
                        onClick = onTogglePlayPause,
                        size = 64.dp,
                        iconSize = 32.dp,
                        testTag = "now_playing_main_orb"
                    )

                    // Next
                    IconButton(
                        onClick = onNext,
                        modifier = Modifier
                            .testTag("now_playing_next_button")
                            .size(46.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Track",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Repeat Mode
                    IconButton(
                        onClick = onToggleRepeat,
                        modifier = Modifier.size(42.dp)
                    ) {
                        val icon = when (playbackState.repeatMode) {
                            RepeatMode.ONE -> Icons.Default.RepeatOne
                            else -> Icons.Default.Repeat
                        }
                        val color = when (playbackState.repeatMode) {
                            RepeatMode.OFF -> OnSurfaceVariant.copy(alpha = 0.6f)
                            else -> CyberMint
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = "Repeat",
                            tint = color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 7. Bottom Context Pills (Lyrics, Output Device, Queue)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lyrics Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .testTag("now_playing_lyrics_button")
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                        .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                        .clickable { onOpenLyrics() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "LYRICS",
                        color = Color.White,
                        fontFamily = MonospaceFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                }

                // Audio Output Hub Pill
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                        .border(1.dp, CyberMint.copy(alpha = 0.3f), CircleShape)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = CyberMint,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "MAX EAR (2) • SPATIAL",
                            color = CyberMint,
                            fontFamily = MonospaceFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Queue Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .testTag("now_playing_queue_button")
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                        .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                        .clickable { onOpenQueue() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "QUEUE",
                            color = Color.White,
                            fontFamily = MonospaceFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
