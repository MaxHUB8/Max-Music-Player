package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LiquidCyberDarkColorScheme = darkColorScheme(
    primary = NeonPurpleLight,
    onPrimary = Color(0xFF22005D),
    primaryContainer = NeonPurple,
    onPrimaryContainer = Color(0xFFF2E7FE),
    secondary = NeonBlueLight,
    onSecondary = Color(0xFF001F5C),
    secondaryContainer = NeonBlue,
    onSecondaryContainer = Color(0xFFE0E7FF),
    tertiary = CyberMint,
    onTertiary = Color(0xFF00382F),
    tertiaryContainer = CyberMintLight,
    onTertiaryContainer = Color(0xFF00201A),
    background = AmoledBlack,
    onBackground = OnSurfaceWhite,
    surface = SurfaceContainerLow,
    onSurface = OnSurfaceWhite,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = OnSurfaceVariant,
    outline = OutlineMuted,
    outlineVariant = OutlineVariantDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to true for AMOLED aesthetic
    themeStyle: MaxThemeStyle = MaxThemeStyle.LIQUID_CYBERNETIC,
    content: @Composable () -> Unit
) {
    val colorScheme = LiquidCyberDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = Color.Transparent.toArgb()
                it.navigationBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(it, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
