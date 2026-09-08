package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Playlist
import com.example.model.Song
import com.example.ui.components.AudioBadge
import com.example.ui.components.liquidGlass
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyberMint
import com.example.ui.theme.MonospaceFontFamily
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleLight
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SurroundSound
import com.example.data.local.PlayHistoryEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlaylistsScreen(
    playlists: List<Playlist>,
    favorites: List<Song>,
    recentHistory: List<PlayHistoryEntity> = emptyList(),
    onPlaySong: (Song, List<Song>) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onDeletePlaylist: (Long) -> Unit = {},
    onPlayHistoryItem: (PlayHistoryEntity) -> Unit = {},
    onClearHistory: () -> Unit = {},
    onDeleteHistoryItem: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "NEW PLAYLIST",
                    color = Color.White,
                    fontFamily = MonospaceFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    placeholder = { Text("Enter playlist name", color = OnSurfaceVariant) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_playlist_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            onCreatePlaylist(newPlaylistName.trim())
                            newPlaylistName = ""
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("CREATE", fontFamily = MonospaceFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("CANCEL", color = OnSurfaceVariant, fontFamily = MonospaceFontFamily)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }

    LazyColumn(
        modifier = modifier
            .testTag("playlists_screen")
            .fillMaxSize()
            .background(AmoledBlack),
        contentPadding = PaddingValues(bottom = 140.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PLAYLISTS & MIXES",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "CURATED AUDIO COLLECTIONS",
                        color = OnSurfaceVariant,
                        fontFamily = MonospaceFontFamily,
                        fontSize = 9.sp,
                        letterSpacing = 1.2.sp
                    )
                }

                // Add Playlist Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .testTag("create_playlist_button")
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NeonPurple, NeonBlue)
                            )
                        )
                        .clickable { showCreateDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Playlist",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Favorites Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .liquidGlass(
                        shape = RoundedCornerShape(26.dp),
                        backgroundColor = Color(0x221A1526),
                        borderColor = Color.White.copy(alpha = 0.2f),
                        glowColor = CyberMint.copy(alpha = 0.2f),
                        elevation = 12.dp
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(CyberMint.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = CyberMint,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "FAVORITES",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${favorites.size} saved tracks",
                                    color = OnSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontFamily = MonospaceFontFamily
                                )
                            }
                        }

                        AudioBadge(text = "DYNAMIC", color = CyberMint)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Play All Button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(NeonPurple, NeonBlue)
                                    )
                                )
                                .clickable {
                                    if (favorites.isNotEmpty()) {
                                        onPlaySong(favorites.first(), favorites)
                                    }
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "PLAY ALL",
                                    color = Color.White,
                                    fontFamily = MonospaceFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Shuffle Button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(SurfaceContainerLow)
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(22.dp))
                                .clickable {
                                    if (favorites.isNotEmpty()) {
                                        val shuffled = favorites.shuffled()
                                        onPlaySong(shuffled.first(), shuffled)
                                    }
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shuffle,
                                    contentDescription = null,
                                    tint = CyberMint,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "SHUFFLE",
                                    color = Color.White,
                                    fontFamily = MonospaceFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Custom Playlists Section
        item {
            Text(
                text = "USER PLAYLISTS (${playlists.size})",
                color = Color.White,
                fontFamily = MonospaceFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
            )
        }

        if (playlists.isEmpty()) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = "No custom playlists created yet.\nTap '+' above to create your first mix.",
                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                        fontFamily = MonospaceFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            items(playlists) { playlist ->
                PlaylistItemRow(
                    playlist = playlist,
                    onDelete = { onDeletePlaylist(playlist.id) }
                )
            }
        }

        // Recent Play History Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = CyberMint,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "RECENT PLAY HISTORY (${recentHistory.size})",
                        color = Color.White,
                        fontFamily = MonospaceFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        letterSpacing = 1.sp
                    )
                }

                if (recentHistory.isNotEmpty()) {
                    Text(
                        text = "CLEAR ALL",
                        color = CyberMint,
                        fontFamily = MonospaceFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onClearHistory() }
                            .padding(4.dp)
                    )
                }
            }
        }

        if (recentHistory.isEmpty()) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Your listened tracks will appear here in high-fidelity history.",
                        color = OnSurfaceVariant.copy(alpha = 0.5f),
                        fontFamily = MonospaceFontFamily,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            items(recentHistory) { item ->
                PlayHistoryItemRow(
                    item = item,
                    onClick = { onPlayHistoryItem(item) },
                    onDelete = { onDeleteHistoryItem(item.id) }
                )
            }
        }
    }
}

@Composable
private fun PlaylistItemRow(
    playlist: Playlist,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* open playlist */ }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QueueMusic,
                contentDescription = null,
                tint = NeonPurpleLight,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Text(
                text = "${playlist.songCount} tracks",
                color = OnSurfaceVariant,
                fontFamily = MonospaceFontFamily,
                fontSize = 11.sp
            )
        }

        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Playlist",
            tint = OnSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .size(20.dp)
                .clickable { onDelete() }
        )
    }
}

@Composable
private fun PlayHistoryItemRow(
    item: PlayHistoryEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val timeAgo = remember(item.playedAt) {
        val diff = System.currentTimeMillis() - item.playedAt
        when {
            diff < 60_000L -> "Just now"
            diff < 3600_000L -> "${diff / 60_000L}m ago"
            diff < 86400_000L -> "${diff / 3600_000L}h ago"
            else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(item.playedAt))
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = CyberMint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (item.isDolby) {
                    AudioBadge(
                        text = "DOLBY",
                        color = NeonPurpleLight,
                        icon = Icons.Default.SurroundSound
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.artist,
                    color = OnSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Text(
                    text = "•",
                    color = OnSurfaceVariant.copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
                Text(
                    text = timeAgo,
                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                    fontFamily = MonospaceFontFamily,
                    fontSize = 10.sp
                )
            }
        }

        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove History Item",
            tint = OnSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier
                .size(18.dp)
                .clickable { onDelete() }
        )
    }
}
