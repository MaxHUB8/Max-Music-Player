package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple

@Composable
fun NeonOrb(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    iconSize: Dp = 32.dp,
    testTag: String = "neon_playback_orb"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "orb_scale"
    )

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            NeonPurple,
            Color(0xFF9E79FF),
            NeonBlue
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .testTag(testTag)
            .scale(scale)
            .size(size)
            .shadow(
                elevation = 20.dp,
                shape = CircleShape,
                ambientColor = NeonPurple.copy(alpha = 0.8f),
                spotColor = NeonBlue.copy(alpha = 0.8f)
            )
            .clip(CircleShape)
            .background(gradientBrush)
            .border(
                width = 1.5.dp,
                color = Color.White.copy(alpha = 0.35f),
                shape = CircleShape
            )
            .drawBehind {
                // High-gloss specular highlight (top hemisphere)
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.4f), Color.Transparent),
                        startY = 0f,
                        endY = this.size.height * 0.55f
                    )
                )
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause track" else "Play track",
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}
