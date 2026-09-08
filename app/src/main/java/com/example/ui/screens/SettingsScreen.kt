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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.example.ui.components.AudioBadge
import com.example.ui.components.liquidGlass
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyberMint
import com.example.ui.theme.MaxThemeStyle
import com.example.ui.theme.MonospaceFontFamily
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleLight
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow

@Composable
fun SettingsScreen(
    currentTheme: MaxThemeStyle,
    onSelectTheme: (MaxThemeStyle) -> Unit,
    onRescanLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    var gapless by remember { mutableStateOf(true) }
    var highResOutput by remember { mutableStateOf(true) }
    var btResume by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .testTag("settings_screen")
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
                        text = "SYSTEM & ENGINE",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "DSP KERNEL & AESTHETICS",
                        color = OnSurfaceVariant,
                        fontFamily = MonospaceFontFamily,
                        fontSize = 9.sp,
                        letterSpacing = 1.2.sp
                    )
                }

                AudioBadge(text = "V4.2 READY", color = CyberMint)
            }
        }

        // Section: Visual Themes
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "VISUAL AESTHETIC PALETTES",
                    color = Color.White,
                    fontFamily = MonospaceFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemeOptionCard(
                        title = "CYBERNETIC",
                        accent = NeonPurple,
                        isSelected = currentTheme == MaxThemeStyle.LIQUID_CYBERNETIC,
                        onClick = { onSelectTheme(MaxThemeStyle.LIQUID_CYBERNETIC) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeOptionCard(
                        title = "BLUE CYAN",
                        accent = NeonBlue,
                        isSelected = currentTheme == MaxThemeStyle.BLUE_CYAN,
                        onClick = { onSelectTheme(MaxThemeStyle.BLUE_CYAN) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemeOptionCard(
                        title = "PURPLE PINK",
                        accent = Color(0xFFFF528E),
                        isSelected = currentTheme == MaxThemeStyle.PURPLE_PINK,
                        onClick = { onSelectTheme(MaxThemeStyle.PURPLE_PINK) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeOptionCard(
                        title = "MONOCHROME",
                        accent = Color(0xFFCCCCCC),
                        isSelected = currentTheme == MaxThemeStyle.MONOCHROME,
                        onClick = { onSelectTheme(MaxThemeStyle.MONOCHROME) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: Audio DSP Engine Settings
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "DSP PLAYBACK ENGINE",
                    color = Color.White,
                    fontFamily = MonospaceFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            shape = RoundedCornerShape(24.dp),
                            backgroundColor = Color(0x1F151422),
                            elevation = 8.dp
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        SettingToggleRow(
                            title = "Gapless Playback",
                            subtitle = "Seamless track transitions with 0ms delay",
                            checked = gapless,
                            onCheckedChange = { gapless = it }
                        )

                        SettingToggleRow(
                            title = "High-Res 24-Bit / 96kHz Output",
                            subtitle = "Bypass Android OS downsampling mixer",
                            checked = highResOutput,
                            onCheckedChange = { highResOutput = it }
                        )

                        SettingToggleRow(
                            title = "Bluetooth Auto-Resume",
                            subtitle = "Resume playback when wireless headset reconnects",
                            checked = btResume,
                            onCheckedChange = { btResume = it }
                        )
                    }
                }
            }
        }

        // Section: Library Scanner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "STORAGE & RE-INDEXING",
                    color = Color.White,
                    fontFamily = MonospaceFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(SurfaceContainerLow)
                        .border(1.dp, CyberMint.copy(alpha = 0.4f), RoundedCornerShape(26.dp))
                        .clickable { onRescanLibrary() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = CyberMint,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "RESCAN DEVICE AUDIO FILES",
                            color = Color.White,
                            fontFamily = MonospaceFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // About MAX Audio
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .liquidGlass(
                        shape = RoundedCornerShape(22.dp),
                        backgroundColor = Color(0x18151420),
                        elevation = 6.dp
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MAX MUSIC PLAYER",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        AudioBadge(text = "BUILD 2026.1", color = NeonPurpleLight)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Crafted with 2026 Transparent Liquid Glass design, 10-band FP32 DSP equalizer, MediaSession background service, and Nothing OS minimalist telemetry.",
                        color = OnSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    accent: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) SurfaceContainerHigh else SurfaceContainerLow)
            .border(
                width = 1.dp,
                color = if (isSelected) accent else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accent)
                )
                Text(
                    text = title,
                    color = Color.White,
                    fontFamily = MonospaceFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = accent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 13.5.sp
            )
            Text(
                text = subtitle,
                color = OnSurfaceVariant,
                fontSize = 11.5.sp,
                lineHeight = 15.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = CyberMint,
                uncheckedThumbColor = OnSurfaceVariant,
                uncheckedTrackColor = SurfaceContainerHigh
            )
        )
    }
}
