package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderHighlight
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceContainerLow

fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(28.dp),
    backgroundColor: Color = Color(0x1D1B1B26), // 8-15% translucent glass surface
    borderColor: Color = GlassBorder,
    highlightColor: Color = GlassBorderHighlight,
    glowColor: Color = NeonPurple.copy(alpha = 0.18f),
    elevation: Dp = 12.dp
): Modifier {
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.35f),
            highlightColor,
            borderColor.copy(alpha = 0.2f),
            Color.Transparent
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = glowColor,
            spotColor = glowColor
        )
        .clip(shape)
        .background(backgroundColor)
        .border(
            border = BorderStroke(1.dp, borderBrush),
            shape = shape
        )
        .drawBehind {
            // Subtle top-left specular highlight sheen
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.1f),
                    radius = size.width * 0.5f
                )
            )
        }
}

fun Modifier.neonAmbientGlow(
    glowColor: Color = NeonPurple,
    alpha: Float = 0.25f,
    radius: Float = 160f
): Modifier = this.drawBehind {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(glowColor.copy(alpha = alpha), Color.Transparent),
            center = Offset(size.width / 2f, size.height / 2f),
            radius = radius
        )
    )
}
