package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LibraryFilter
import com.example.ui.theme.CyberMint
import com.example.ui.theme.MonospaceFontFamily
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleLight
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow

@Composable
fun AudioBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CyberMint,
    icon: ImageVector? = null,
    isPulsing: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.35f),
                shape = CircleShape
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }

        Text(
            text = text.uppercase(),
            color = color,
            fontFamily = MonospaceFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 9.sp,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun LibraryCategoryPills(
    selectedFilter: LibraryFilter,
    onSelectFilter: (LibraryFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LibraryFilter.values().forEach { filter ->
            val isSelected = filter == selectedFilter
            val label = when (filter) {
                LibraryFilter.ALL -> "All Tracks"
                LibraryFilter.ALBUMS -> "Albums"
                LibraryFilter.ARTISTS -> "Artists"
                LibraryFilter.FOLDERS -> "Folders"
                LibraryFilter.CLOUD_SYNC -> "Cloud Sync"
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .testTag("filter_pill_${filter.name.lowercase()}")
                    .clip(CircleShape)
                    .clickable { onSelectFilter(filter) }
                    .then(
                        if (isSelected) {
                            Modifier
                                .background(SurfaceContainerHighest.copy(alpha = 0.85f))
                                .border(
                                    width = 1.dp,
                                    color = NeonPurpleLight.copy(alpha = 0.6f),
                                    shape = CircleShape
                                )
                        } else {
                            Modifier
                                .background(SurfaceContainerLow.copy(alpha = 0.65f))
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.08f),
                                    shape = CircleShape
                                )
                        }
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CyberMint)
                        )
                    } else if (filter == LibraryFilter.CLOUD_SYNC) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = NeonBlue,
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    Text(
                        text = label,
                        color = if (isSelected) Color.White else OnSurfaceVariant,
                        fontFamily = MonospaceFontFamily,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
