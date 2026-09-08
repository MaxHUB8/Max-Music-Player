package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberMint
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleLight

@Composable
fun AudioVisualizerWave(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 44.dp,
    barCount: Int = 40
) {
    val displayAmps = if (amplitudes.size >= barCount) amplitudes.take(barCount) else List(barCount) { 0.15f }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        displayAmps.forEachIndexed { index, amp ->
            val animatedHeight by animateFloatAsState(
                targetValue = amp.coerceIn(0.1f, 1f),
                animationSpec = tween(durationMillis = 120),
                label = "vis_bar_$index"
            )

            val barColor = when (index % 4) {
                0 -> NeonPurple
                1 -> NeonPurpleLight
                2 -> CyberMint
                else -> NeonBlue
            }

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(animatedHeight)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                barColor,
                                barColor.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
        }
    }
}
