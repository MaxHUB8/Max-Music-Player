package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NavTab
import com.example.ui.theme.CyberMint
import com.example.ui.theme.MonospaceFontFamily
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OnSurfaceVariant

@Composable
fun FloatingBottomNav(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    isPlaying: Boolean,
    onCenterOrbClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(68.dp)
            .liquidGlass(
                shape = RoundedCornerShape(32.dp),
                backgroundColor = Color(0x2812121A),
                borderColor = Color.White.copy(alpha = 0.2f),
                glowColor = NeonPurple.copy(alpha = 0.25f),
                elevation = 16.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Library / Home
            NavItem(
                icon = Icons.Default.LibraryMusic,
                label = "LIBRARY",
                isSelected = currentTab == NavTab.LIBRARY || currentTab == NavTab.HOME,
                onClick = { onTabSelected(NavTab.LIBRARY) },
                testTag = "nav_tab_library"
            )

            // 2. Playlists
            NavItem(
                icon = Icons.Default.QueueMusic,
                label = "PLAYLISTS",
                isSelected = currentTab == NavTab.PLAYLISTS,
                onClick = { onTabSelected(NavTab.PLAYLISTS) },
                testTag = "nav_tab_playlists"
            )

            // 3. Center Playback Orb
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(y = (-4).dp)
            ) {
                NeonOrb(
                    isPlaying = isPlaying,
                    onClick = onCenterOrbClick,
                    size = 50.dp,
                    iconSize = 26.dp,
                    testTag = "nav_center_orb"
                )
            }

            // 4. Equalizer
            NavItem(
                icon = Icons.Default.GraphicEq,
                label = "DSP EQ",
                isSelected = currentTab == NavTab.EQUALIZER,
                onClick = { onTabSelected(NavTab.EQUALIZER) },
                testTag = "nav_tab_equalizer"
            )

            // 5. Settings
            NavItem(
                icon = Icons.Default.Settings,
                label = "SYSTEM",
                isSelected = currentTab == NavTab.SETTINGS,
                onClick = { onTabSelected(NavTab.SETTINGS) },
                testTag = "nav_tab_settings"
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .testTag(testTag)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color.White else OnSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(22.dp)
        )

        Text(
            text = label,
            color = if (isSelected) CyberMint else OnSurfaceVariant.copy(alpha = 0.5f),
            fontFamily = MonospaceFontFamily,
            fontSize = 8.5.sp,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(top = 3.dp)
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(CyberMint)
            )
        }
    }
}
