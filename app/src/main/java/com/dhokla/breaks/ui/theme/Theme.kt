package com.dhokla.breaks.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.dhokla.breaks.data.ThemeMode

fun ThemeMode.resolveDark(systemDark: Boolean): Boolean = when (this) {
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
    ThemeMode.SYSTEM -> systemDark
}

val LocalCalm = staticCompositionLocalOf { CalmLight }

private val LightColors = lightColorScheme(
    primary = Violet,
    onPrimary = Color.White,
    primaryContainer = VioletContainer,
    onPrimaryContainer = OnVioletContainer,
    secondary = Color(0xFF675B80),
    onSecondary = Color.White,
    background = Color(0xFFF1EBFA),
    onBackground = InkViolet,
    surface = Color(0xFFF1EBFA),
    onSurface = InkViolet,
    surfaceVariant = LilacMist,
    onSurfaceVariant = InkVioletSoft,
    outline = Color(0x242A2240),
    outlineVariant = Color(0x162A2240)
)

private val DarkColors = darkColorScheme(
    primary = LavenderGlow,
    onPrimary = OnLavenderGlow,
    primaryContainer = LavenderGlowContainer,
    onPrimaryContainer = InkNight,
    secondary = Color(0xFFACA2C4),
    onSecondary = Color(0xFF1D1830),
    background = NightBase,
    onBackground = InkNight,
    surface = NightBase,
    onSurface = InkNight,
    surfaceVariant = NightLift,
    onSurfaceVariant = InkNightSoft,
    outline = Color(0x2EE7EBE1),
    outlineVariant = Color(0x1FE7EBE1)
)

@Composable
fun BreaksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val calm = if (darkTheme) CalmDark else CalmLight
    CompositionLocalProvider(LocalCalm provides calm) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = Typography,
            content = content
        )
    }
}
