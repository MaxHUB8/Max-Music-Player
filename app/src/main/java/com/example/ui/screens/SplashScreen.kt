package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AudioBadge
import com.example.ui.components.liquidGlass
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyberMint
import com.example.ui.theme.MonospaceFontFamily
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleLight
import com.example.ui.theme.OnSurfaceVariant

@Composable
fun SplashScreen(
    onEnterSoundstage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc_rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "disc_pulse"
    )

    Box(
        modifier = modifier
            .testTag("splash_screen")
            .fillMaxSize()
            .background(AmoledBlack)
            .drawBehind {
                // Cybernetic subtle backdrop aurora
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonPurple.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.38f),
                        radius = size.width * 0.7f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonBlue.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.45f),
                        radius = size.width * 0.6f
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            // Top Telemetry Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AudioBadge(
                    text = "DSP KERNEL V4.2",
                    color = NeonPurpleLight,
                    icon = Icons.Default.GraphicEq
                )

                AudioBadge(
                    text = "384kHz / 32Bit",
                    color = CyberMint
                )
            }

            // Centerpiece: Holographic Glass Vinyl Disc
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(280.dp)
                    .rotate(rotation)
            ) {
                // Vinyl disc concentric grooves
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxR = size.width / 2f

                    // Outer glass refraction halo
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                NeonPurple.copy(alpha = 0.4f),
                                NeonBlue.copy(alpha = 0.5f),
                                CyberMint.copy(alpha = 0.3f),
                                NeonPurple.copy(alpha = 0.4f)
                            )
                        ),
                        radius = maxR - 4f,
                        style = Stroke(width = 3f)
                    )

                    // Concentric microscopic grooves
                    for (i in 1..9) {
                        val r = (maxR * 0.28f) + (i * (maxR * 0.07f))
                        drawCircle(
                            color = Color.White.copy(alpha = if (i % 2 == 0) 0.08f else 0.04f),
                            radius = r,
                            style = Stroke(width = 1f)
                        )
                    }
                }

                // Center Glass Hub with "MX" Branding
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF1B182B),
                                    Color(0xFF090810)
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(NeonPurple, NeonBlue, CyberMint)
                            ),
                            shape = CircleShape
                        )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "MX",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "ENGINE",
                            color = CyberMint,
                            fontFamily = MonospaceFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 8.sp,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            // Bottom Information & Enter Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "MAX",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    letterSpacing = 3.sp
                )

                Text(
                    text = "TRANSPARENT AUDIO ENGINE 2026",
                    color = OnSurfaceVariant,
                    fontFamily = MonospaceFontFamily,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // Buffer Telemetry Glass Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            shape = RoundedCornerShape(20.dp),
                            backgroundColor = Color(0x18181726),
                            elevation = 8.dp
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "INITIALIZING 120HZ AUDIO BUFFER",
                                color = Color.White.copy(alpha = 0.85f),
                                fontFamily = MonospaceFontFamily,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "LATENCY: 0.8MS",
                                color = CyberMint,
                                fontFamily = MonospaceFontFamily,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Buffer Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(NeonPurple, NeonBlue, CyberMint)
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "STATUS: FP32 ACCURATE • ULTRA-LOW JITTER",
                            color = OnSurfaceVariant.copy(alpha = 0.7f),
                            fontFamily = MonospaceFontFamily,
                            fontSize = 8.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom feature badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AudioBadge(text = "HI-RES AUDIO", color = NeonPurpleLight)
                    AudioBadge(text = "LDAC 990K", color = NeonBlue)
                    AudioBadge(text = "SPATIAL 3D", color = CyberMint, icon = Icons.Default.SurroundSound)
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Enter Soundstage Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .testTag("enter_soundstage_button")
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(NeonPurple, NeonBlue)
                            )
                        )
                        .clickable { onEnterSoundstage() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ENTER SOUNDSTAGE",
                            color = Color.White,
                            fontFamily = MonospaceFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.2.sp
                        )

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
