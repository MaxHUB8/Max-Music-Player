package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// AMOLED Canvas & Sub-surfaces
val AmoledBlack = Color(0xFF000000)
val SurfaceContainerLowest = Color(0xFF0E0E0E)
val SurfaceContainerLow = Color(0xFF131318)
val SurfaceContainer = Color(0xFF1B1B22)
val SurfaceContainerHigh = Color(0xFF262632)
val SurfaceContainerHighest = Color(0xFF353545)

// Liquid Cybernetic Neons
val NeonPurple = Color(0xFF8B5CFF)
val NeonPurpleLight = Color(0xFFCFBDFF)
val NeonPurpleGlow = Color(0x708B5CFF)

val NeonBlue = Color(0xFF4F7CFF)
val NeonBlueLight = Color(0xFFB5C4FF)
val NeonBlueGlow = Color(0x604F7CFF)

val CyberMint = Color(0xFF00DFC1)
val CyberMintLight = Color(0xFF26FEDC)
val CyberMintGlow = Color(0x7000DFC1)

// Glass Highlights & Text
val GlassSurface = Color(0x18FFFFFF)
val GlassBorder = Color(0x30FFFFFF)
val GlassBorderHighlight = Color(0x55CFBDFF)
val GlassSpecular = Color(0x40FFFFFF)

val OnSurfaceWhite = Color(0xFFF2F2F7)
val OnSurfaceVariant = Color(0xFFCBC3D7)
val OutlineMuted = Color(0xFF948EA1)
val OutlineVariantDark = Color(0xFF494455)

// Theme Palette options
enum class MaxThemeStyle {
    LIQUID_CYBERNETIC, // Purple + Blue + Mint (Default)
    BLUE_CYAN,         // Blue + Cyan
    PURPLE_PINK,       // Purple + Magenta
    MONOCHROME         // Silver + Pure White
}
