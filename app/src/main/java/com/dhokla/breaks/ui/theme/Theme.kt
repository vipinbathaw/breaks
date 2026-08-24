package com.dhokla.breaks.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalCalm = staticCompositionLocalOf { CalmLight }

private val LightColors = lightColorScheme(
    primary = Forest,
    onPrimary = Color.White,
    primaryContainer = ForestContainer,
    onPrimaryContainer = OnForest,
    secondary = Color(0xFF5E6B60),
    onSecondary = Color.White,
    background = Cream,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink,
    surfaceVariant = CreamDeep,
    onSurfaceVariant = InkSoft,
    outline = Color(0x24221F17),
    outlineVariant = Color(0x16221F17)
)

private val DarkColors = darkColorScheme(
    primary = Fern,
    onPrimary = OnFern,
    primaryContainer = FernContainer,
    onPrimaryContainer = InkNight,
    secondary = Color(0xFFAEB8AB),
    onSecondary = Color(0xFF1B201C),
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
