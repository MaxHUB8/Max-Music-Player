package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Song
import com.example.ui.components.AudioBadge
import com.example.ui.components.liquidGlass
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyberMint
import com.example.ui.theme.MonospaceFontFamily
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.SurfaceContainerLow

private val DEFAULT_LYRICS = listOf(
    "Neon horizons burning through the night",
    "Digital pulses in the crystal light",
    "Liquid reflections dancing on the chrome",
    "Synthetic heartbeat calling us back home",
    "[CHORUS]",
    "Break through the frequencies, ride on the sound",
    "Where lossless harmonics are forever unbound",
    "384 kilohertz running through my veins",
    "Pure cyber energy washing off the chains",
    "[SOLO - SYNTHESIZER 120HZ]",
    "Signals align in the infinite sphere",
    "Every resonance crystal and clear",
    "MAX soundstage opening wide and deep",
    "Promises that the future will keep"
)

@Composable
fun LyricsSheet(
    song: Song?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag("lyrics_sheet")
            .fillMaxSize()
            .background(AmoledBlack.copy(alpha = 0.95f))
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SYNCHRONIZED LYRICS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "${song?.title ?: "Current Track"} • ${song?.artist ?: ""}",
                        color = OnSurfaceVariant,
                        fontSize = 12.sp,
                        fontFamily = MonospaceFontFamily
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Lyrics",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Lyrics Scroll List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(DEFAULT_LYRICS) { index, line ->
                    val isActive = index == 2 || index == 3 // Simulated active line
                    Text(
                        text = line,
                        color = if (isActive) CyberMint else Color.White.copy(alpha = 0.5f),
                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                        fontSize = if (isActive) 20.sp else 16.sp,
                        lineHeight = if (isActive) 28.sp else 24.sp
                    )
                }
            }
        }
    }
}
