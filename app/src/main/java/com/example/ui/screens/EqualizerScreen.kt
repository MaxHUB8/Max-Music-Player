package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EqualizerState
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
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow

private val EQ_FREQUENCIES = listOf(
    "31Hz", "62Hz", "125Hz", "250Hz", "500Hz", "1k", "2k", "4k", "8k", "16k"
)

private val EQ_PRESETS = mapOf(
    "Cyber Bass" to listOf(6, 8, 4, 2, -1, 0, 3, 5, 7, 9),
    "Vocal Crystal" to listOf(-2, -1, 0, 2, 5, 6, 4, 2, 1, 0),
    "Electronic 120Hz" to listOf(7, 9, 6, 2, 0, 1, 4, 6, 8, 8),
    "Acoustic" to listOf(3, 2, 1, 1, 2, 3, 4, 3, 2, 2),
    "Rock" to listOf(5, 4, 2, -1, -2, 0, 3, 5, 6, 7),
    "Flat" to listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
)

@Composable
fun EqualizerScreen(
    equalizerState: EqualizerState,
    onUpdateEqualizer: (EqualizerState) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .testTag("equalizer_screen")
            .fillMaxSize()
            .background(AmoledBlack),
        contentPadding = PaddingValues(bottom = 140.dp)
    ) {
        // 1. Header with Master Power Switch
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "DSP EQUALIZER",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            letterSpacing = 1.sp
                        )
                        AudioBadge(
                            text = "10-BAND",
                            color = CyberMint
                        )
                    }

                    Text(
                        text = "FP32 PARAMETRIC ENGINE",
                        color = OnSurfaceVariant,
                        fontFamily = MonospaceFontFamily,
                        fontSize = 9.sp,
                        letterSpacing = 1.2.sp
                    )
                }

                // Master Power Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (equalizerState.isEnabled) "ACTIVE" else "BYPASS",
                        color = if (equalizerState.isEnabled) CyberMint else OnSurfaceVariant,
                        fontFamily = MonospaceFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )

                    Switch(
                        checked = equalizerState.isEnabled,
                        onCheckedChange = { isChecked ->
                            onUpdateEqualizer(equalizerState.copy(isEnabled = isChecked))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CyberMint,
                            uncheckedThumbColor = OnSurfaceVariant,
                            uncheckedTrackColor = SurfaceContainerHigh
                        ),
                        modifier = Modifier.testTag("eq_power_switch")
                    )
                }
            }
        }

        // 1.5. Dedicated Dolby Atmos / Dolby Audio Spatial Soundstage Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .liquidGlass(
                        shape = RoundedCornerShape(26.dp),
                        backgroundColor = Color(0x22131A2E),
                        borderColor = if (equalizerState.isDolbyEnabled) CyberMint.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.12f),
                        glowColor = if (equalizerState.isDolbyEnabled) CyberMint.copy(alpha = 0.25f) else Color.Transparent,
                        elevation = 14.dp
                    )
                    .padding(16.dp)
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (equalizerState.isDolbyEnabled)
                                            Brush.linearGradient(listOf(Color(0xFF00E5FF), Color(0xFF8B5CFF)))
                                        else
                                            Brush.linearGradient(listOf(SurfaceContainerHigh, SurfaceContainerLow))
                                    )
                            ) {
                                Text(
                                    text = "D",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = MonospaceFontFamily,
                                    fontSize = 18.sp
                                )
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "DOLBY AUDIO™",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        letterSpacing = 0.8.sp
                                    )
                                    AudioBadge(
                                        text = if (equalizerState.isDolbyEnabled) "ATMOS 3D" else "BYPASS",
                                        color = if (equalizerState.isDolbyEnabled) CyberMint else OnSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "BINAURAL SPATIAL SOUNDSTAGE & HEADROOM",
                                    color = OnSurfaceVariant,
                                    fontFamily = MonospaceFontFamily,
                                    fontSize = 8.5.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Switch(
                            checked = equalizerState.isDolbyEnabled,
                            onCheckedChange = { isChecked ->
                                onUpdateEqualizer(equalizerState.copy(isDolbyEnabled = isChecked))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CyberMint,
                                uncheckedThumbColor = OnSurfaceVariant,
                                uncheckedTrackColor = SurfaceContainerHigh
                            ),
                            modifier = Modifier.testTag("dolby_audio_switch")
                        )
                    }

                    if (equalizerState.isDolbyEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Dolby Profiles Row
                        val dolbyProfiles = listOf(
                            "ATMOS SPATIAL",
                            "DOLBY CINEMA",
                            "DOLBY MUSIC",
                            "DOLBY VISION MASTER"
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(dolbyProfiles) { profile ->
                                val isSelected = equalizerState.dolbyProfile == profile
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSelected) CyberMint.copy(alpha = 0.2f) else SurfaceContainerLow)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) CyberMint else Color.White.copy(alpha = 0.1f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onUpdateEqualizer(equalizerState.copy(dolbyProfile = profile))
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = profile,
                                        color = if (isSelected) CyberMint else OnSurfaceVariant,
                                        fontFamily = MonospaceFontFamily,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Surround Depth & Dialogue Clarity Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Surround Depth Interactive Chip
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SurfaceContainerLow)
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        val nextDepth = if (equalizerState.dolbySurroundPct >= 100) 40 else equalizerState.dolbySurroundPct + 20
                                        onUpdateEqualizer(equalizerState.copy(dolbySurroundPct = nextDepth))
                                    }
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "SPATIAL DEPTH",
                                        color = OnSurfaceVariant,
                                        fontFamily = MonospaceFontFamily,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${equalizerState.dolbySurroundPct}% EXPANDED",
                                        color = CyberMint,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // Dynamic Headroom / Dialogue Boost Chip
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SurfaceContainerLow)
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        onUpdateEqualizer(equalizerState.copy(dolbyDialogueClarity = !equalizerState.dolbyDialogueClarity))
                                    }
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "VOCAL CLARITY",
                                        color = OnSurfaceVariant,
                                        fontFamily = MonospaceFontFamily,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (equalizerState.dolbyDialogueClarity) "CRYSTAL DIALOGUE" else "BALANCED",
                                        color = NeonPurpleLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Preset Pills Row
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(EQ_PRESETS.keys.toList()) { presetName ->
                    val isSelected = equalizerState.presetName == presetName
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .testTag("eq_preset_${presetName.lowercase().replace(" ", "_")}")
                            .clip(CircleShape)
                            .background(if (isSelected) SurfaceContainerHighest else SurfaceContainerLow)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) NeonPurpleLight else Color.White.copy(alpha = 0.08f),
                                shape = CircleShape
                            )
                            .clickable {
                                val bands = EQ_PRESETS[presetName] ?: equalizerState.bandLevels
                                onUpdateEqualizer(
                                    equalizerState.copy(
                                        presetName = presetName,
                                        bandLevels = bands
                                    )
                                )
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
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
                            }
                            Text(
                                text = presetName,
                                color = if (isSelected) Color.White else OnSurfaceVariant,
                                fontFamily = MonospaceFontFamily,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. Curved Spline Real-Time Graph + Frequency Rack
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .liquidGlass(
                        shape = RoundedCornerShape(26.dp),
                        backgroundColor = Color(0x22161524),
                        borderColor = Color.White.copy(alpha = 0.18f),
                        glowColor = NeonPurple.copy(alpha = 0.2f),
                        elevation = 12.dp
                    )
                    .padding(16.dp)
            ) {
                Column {
                    // Curved Neon Spline Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val midY = h / 2f

                            // Center baseline (0 dB)
                            drawLine(
                                color = Color.White.copy(alpha = 0.12f),
                                start = Offset(0f, midY),
                                end = Offset(w, midY),
                                strokeWidth = 1.5f
                            )

                            // Construct cubic Bezier spline through band levels
                            val bandCount = equalizerState.bandLevels.size
                            if (bandCount > 1) {
                                val stepX = w / (bandCount - 1)
                                val points = equalizerState.bandLevels.mapIndexed { index, db ->
                                    val x = index * stepX
                                    // Map -12..+12 dB to h..0
                                    val normalized = (db + 12f) / 24f
                                    val y = (1f - normalized) * (h - 16f) + 8f
                                    Offset(x, y)
                                }

                                val path = Path().apply {
                                    moveTo(points.first().x, points.first().y)
                                    for (i in 0 until points.size - 1) {
                                        val p0 = points[i]
                                        val p1 = points[i + 1]
                                        val controlX = (p0.x + p1.x) / 2f
                                        cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                                    }
                                }

                                // Draw glowing curved line
                                drawPath(
                                    path = path,
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(NeonPurple, CyberMint, NeonBlue)
                                    ),
                                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                                )

                                // Draw control dots on the curve
                                points.forEach { pt ->
                                    drawCircle(
                                        color = CyberMint,
                                        radius = 3.dp.toPx(),
                                        center = pt
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 10-Band Vertical Sliders Rack
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        equalizerState.bandLevels.forEachIndexed { index, levelDb ->
                            EqBandColumn(
                                frequency = EQ_FREQUENCIES.getOrElse(index) { "${index}k" },
                                levelDb = levelDb,
                                isEnabled = equalizerState.isEnabled,
                                onLevelChange = { newDb ->
                                    val updated = equalizerState.bandLevels.toMutableList()
                                    updated[index] = newDb
                                    onUpdateEqualizer(
                                        equalizerState.copy(
                                            bandLevels = updated,
                                            presetName = "Custom"
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // 4. Three Sound Enhancement Dials
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "ACOUSTIC ENHANCEMENT",
                    color = Color.White,
                    fontFamily = MonospaceFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Bass Boost Dial Card
                    EnhancementCard(
                        title = "BASS BOOST",
                        valueLabel = "+${equalizerState.bassBoostDb} dB",
                        subtext = "SUB-HARMONIC",
                        color = NeonPurple,
                        fraction = (equalizerState.bassBoostDb / 19f).coerceIn(0f, 1f),
                        onClick = {
                            val next = (equalizerState.bassBoostDb + 3) % 20
                            onUpdateEqualizer(equalizerState.copy(bassBoostDb = next))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Virtualizer Dial Card
                    EnhancementCard(
                        title = "SPATIAL 3D",
                        valueLabel = "${equalizerState.virtualizerPct}%",
                        subtext = "VIRTUALIZER",
                        color = CyberMint,
                        fraction = (equalizerState.virtualizerPct / 100f).coerceIn(0f, 1f),
                        onClick = {
                            val next = if (equalizerState.virtualizerPct >= 100) 0 else equalizerState.virtualizerPct + 20
                            onUpdateEqualizer(equalizerState.copy(virtualizerPct = next))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Reverb Card
                    EnhancementCard(
                        title = "REVERB",
                        valueLabel = equalizerState.reverbPreset,
                        subtext = "ACOUSTIC SPACE",
                        color = NeonBlue,
                        fraction = 0.75f,
                        onClick = {
                            val next = when (equalizerState.reverbPreset) {
                                "Club Glass" -> "Studio"
                                "Studio" -> "Concert Hall"
                                "Concert Hall" -> "Off"
                                else -> "Club Glass"
                            }
                            onUpdateEqualizer(equalizerState.copy(reverbPreset = next))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 5. Action Buttons (Reset Curve & Save Preset)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reset Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceContainerLow)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                        .clickable {
                            val flat = List(10) { 0 }
                            onUpdateEqualizer(
                                equalizerState.copy(
                                    presetName = "Flat",
                                    bandLevels = flat,
                                    bassBoostDb = 0,
                                    virtualizerPct = 0
                                )
                            )
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "RESET CURVE",
                            color = Color.White,
                            fontFamily = MonospaceFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Save Preset Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(NeonPurple, NeonBlue)
                            )
                        )
                        .clickable {
                            onUpdateEqualizer(equalizerState.copy(presetName = "Custom Preset"))
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "SAVE PRESET",
                            color = Color.White,
                            fontFamily = MonospaceFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EqBandColumn(
    frequency: String,
    levelDb: Int,
    isEnabled: Boolean,
    onLevelChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(28.dp)
    ) {
        // Level dB readout
        Text(
            text = if (levelDb > 0) "+$levelDb" else "$levelDb",
            color = if (levelDb != 0) CyberMint else OnSurfaceVariant.copy(alpha = 0.6f),
            fontFamily = MonospaceFontFamily,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Vertical Slider Track
        Box(
            modifier = Modifier
                .height(130.dp)
                .width(22.dp)
                .pointerInput(isEnabled) {
                    if (!isEnabled) return@pointerInput
                    detectVerticalDragGestures { change, _ ->
                        change.consume()
                        val heightPx = size.height
                        val fraction = (1f - (change.position.y / heightPx)).coerceIn(0f, 1f)
                        // Map 0..1 to -12..+12 dB
                        val db = ((fraction * 24f) - 12f).toInt().coerceIn(-12, 12)
                        onLevelChange(db)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Background rail
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(SurfaceContainerHighest)
            )

            // Center notch line at 0 dB
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(1.5.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )

            // Thumb Indicator
            val thumbFraction = ((levelDb + 12f) / 24f).coerceIn(0f, 1f)
            val thumbOffsetY = ((1f - thumbFraction) * 114f).dp

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = thumbOffsetY)
                    .size(16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = if (isEnabled) CyberMint else Color.Transparent,
                        spotColor = if (isEnabled) CyberMint else Color.Transparent
                    )
                    .clip(CircleShape)
                    .background(if (isEnabled) CyberMint else OnSurfaceVariant)
                    .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Frequency Label
        Text(
            text = frequency,
            color = OnSurfaceVariant,
            fontFamily = MonospaceFontFamily,
            fontSize = 8.5.sp
        )
    }
}

@Composable
private fun EnhancementCard(
    title: String,
    valueLabel: String,
    subtext: String,
    color: Color,
    fraction: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceContainerLow)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                color = OnSurfaceVariant,
                fontFamily = MonospaceFontFamily,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = valueLabel,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Gauge bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .background(color)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtext,
                color = color,
                fontFamily = MonospaceFontFamily,
                fontSize = 7.5.sp,
                letterSpacing = 0.8.sp
            )
        }
    }
}
