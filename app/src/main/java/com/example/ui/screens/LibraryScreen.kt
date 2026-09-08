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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Album
import com.example.model.Artist
import com.example.model.Folder
import com.example.model.PlaybackState
import com.example.model.Song
import com.example.ui.LibraryFilter
import com.example.ui.components.AudioBadge
import com.example.ui.components.LibraryCategoryPills
import com.example.ui.components.NeonOrb
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

@Composable
fun LibraryScreen(
    songs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    folders: List<Folder>,
    playbackState: PlaybackState,
    selectedFilter: LibraryFilter,
    onSelectFilter: (LibraryFilter) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSongClick: (Song) -> Unit,
    onToggleFavorite: (Song) -> Unit,
    onOpenEqualizer: () -> Unit,
    isScanning: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .testTag("library_screen")
            .fillMaxSize()
            .background(AmoledBlack),
        contentPadding = PaddingValues(bottom = 140.dp)
    ) {
        // Top Header Console
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "MAX",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                        AudioBadge(
                            text = "120HZ ENGINE",
                            color = CyberMint
                        )
                    }

                    Text(
                        text = "AUDIOPHILE SOUNDSTAGE",
                        color = OnSurfaceVariant,
                        fontFamily = MonospaceFontFamily,
                        fontSize = 9.sp,
                        letterSpacing = 1.5.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // DSP Equalizer shortcut pill
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .testTag("library_eq_shortcut")
                            .clip(CircleShape)
                            .background(SurfaceContainerLow)
                            .border(1.dp, NeonPurple.copy(alpha = 0.4f), CircleShape)
                            .clickable { onOpenEqualizer() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Open Equalizer",
                                tint = NeonPurpleLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "DSP EQ",
                                color = Color.White,
                                fontFamily = MonospaceFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Avatar / System icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(NeonPurple, NeonBlue)
                                )
                            )
                    ) {
                        Text(
                            text = "MX",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Search Bar Glass Pill
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .liquidGlass(
                        shape = RoundedCornerShape(22.dp),
                        backgroundColor = Color(0x18181622),
                        borderColor = Color.White.copy(alpha = 0.15f),
                        elevation = 6.dp
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search tracks, artists, format...",
                                color = OnSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 13.sp
                            ),
                            cursorBrush = SolidColor(CyberMint),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("library_search_input")
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice search",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Filter Category Pills
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                item {
                    LibraryCategoryPills(
                        selectedFilter = selectedFilter,
                        onSelectFilter = onSelectFilter
                    )
                }
            }
        }

        // Scanning Indicator
        if (isScanning) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = CyberMint,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Indexing audio library...",
                        color = OnSurfaceVariant,
                        fontSize = 12.sp,
                        fontFamily = MonospaceFontFamily
                    )
                }
            }
        }

        // Switch content based on filter
        when (selectedFilter) {
            LibraryFilter.ALBUMS -> {
                item {
                    Text(
                        text = "ALBUMS (${albums.size})",
                        color = Color.White,
                        fontFamily = MonospaceFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
                items(albums) { album ->
                    AlbumListItem(album = album)
                }
            }

            LibraryFilter.ARTISTS -> {
                item {
                    Text(
                        text = "ARTISTS (${artists.size})",
                        color = Color.White,
                        fontFamily = MonospaceFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
                items(artists) { artist ->
                    ArtistListItem(artist = artist)
                }
            }

            LibraryFilter.FOLDERS -> {
                item {
                    Text(
                        text = "DIRECTORIES (${folders.size})",
                        color = Color.White,
                        fontFamily = MonospaceFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
                items(folders) { folder ->
                    FolderListItem(folder = folder)
                }
            }

            else -> {
                // Recent Sessions 2x2 Grid (Top 4 songs)
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RECENT SESSIONS",
                                color = Color.White,
                                fontFamily = MonospaceFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = "HI-RES MASTER",
                                color = CyberMint,
                                fontFamily = MonospaceFontFamily,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val recentItems = songs.take(4)
                        for (i in recentItems.indices step 2) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RecentCard(
                                    song = recentItems[i],
                                    isPlaying = playbackState.currentSong?.id == recentItems[i].id && playbackState.isPlaying,
                                    onClick = { onSongClick(recentItems[i]) },
                                    modifier = Modifier.weight(1f)
                                )
                                if (i + 1 < recentItems.size) {
                                    RecentCard(
                                        song = recentItems[i + 1],
                                        isPlaying = playbackState.currentSong?.id == recentItems[i + 1].id && playbackState.isPlaying,
                                        onClick = { onSongClick(recentItems[i + 1]) },
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // Primary Queue / All Tracks Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRIMARY QUEUE (${songs.size})",
                            color = Color.White,
                            fontFamily = MonospaceFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "AUTO-ROUTING",
                            color = OnSurfaceVariant,
                            fontFamily = MonospaceFontFamily,
                            fontSize = 9.sp
                        )
                    }
                }

                // Songs List
                itemsIndexed(songs) { index, song ->
                    val isCurrent = playbackState.currentSong?.id == song.id
                    TrackListItem(
                        rank = index + 1,
                        song = song,
                        isCurrentPlaying = isCurrent && playbackState.isPlaying,
                        isSelected = isCurrent,
                        onClick = { onSongClick(song) },
                        onToggleFavorite = { onToggleFavorite(song) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentCard(
    song: Song,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag("recent_card_${song.id}")
            .aspectRatio(1.25f)
            .liquidGlass(
                shape = RoundedCornerShape(22.dp),
                backgroundColor = Color(0x20181724),
                borderColor = Color.White.copy(alpha = 0.2f),
                glowColor = NeonPurple.copy(alpha = 0.18f),
                elevation = 8.dp
            )
            .clickable { onClick() }
    ) {
        // Ambient background glow from album art
        if (song.albumArtUri != null) {
            AsyncImage(
                model = song.albumArtUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(22.dp)),
                alpha = 0.45f
            )
        }

        // Overlay gradient for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x9005050A),
                            Color(0xFA05050A)
                        )
                    )
                )
        )

        // Card Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AudioBadge(
                    text = song.formatBadge,
                    color = CyberMint
                )

                // Neon mini orb play button
                NeonOrb(
                    isPlaying = isPlaying,
                    onClick = onClick,
                    size = 32.dp,
                    iconSize = 16.dp,
                    testTag = "recent_play_${song.id}"
                )
            }

            Column {
                Text(
                    text = song.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = song.artist,
                    color = OnSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TrackListItem(
    rank: Int,
    song: Song,
    isCurrentPlaying: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Row(
        modifier = Modifier
            .testTag("track_item_${song.id}")
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (isSelected) Color(0x188B5CFF) else Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track Rank or Animated Speaker
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.width(28.dp)
        ) {
            if (isCurrentPlaying) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Playing",
                    tint = CyberMint,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = String.format("%02d", rank),
                    color = if (isSelected) NeonPurpleLight else OnSurfaceVariant.copy(alpha = 0.6f),
                    fontFamily = MonospaceFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Album Art Thumbnail (Squircle)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLow)
        ) {
            if (song.albumArtUri != null) {
                AsyncImage(
                    model = song.albumArtUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = NeonPurple,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Track Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                color = if (isSelected) NeonPurpleLight else Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = song.artist,
                    color = OnSurfaceVariant,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Text(
                    text = "• ${song.formatBadge}",
                    color = CyberMint,
                    fontFamily = MonospaceFontFamily,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Duration
        Text(
            text = song.durationFormatted,
            color = OnSurfaceVariant.copy(alpha = 0.7f),
            fontFamily = MonospaceFontFamily,
            fontSize = 10.5.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Favorite Toggle
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Toggle favorite",
                tint = if (song.isFavorite) CyberMint else OnSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AlbumListItem(album: Album) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            if (album.artworkUri != null) {
                AsyncImage(
                    model = album.artworkUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(imageVector = Icons.Default.Album, contentDescription = null, tint = NeonBlue)
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = album.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                text = "${album.artist} • ${album.songCount} tracks",
                color = OnSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ArtistListItem(artist: Artist) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SurfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NeonPurpleLight)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = artist.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                text = "${artist.songCount} tracks • ${artist.albumCount} albums",
                color = OnSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun FolderListItem(folder: Folder) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = CyberMint)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = folder.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "${folder.songCount} tracks", color = OnSurfaceVariant, fontSize = 12.sp)
        }
    }
}
