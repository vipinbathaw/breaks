package com.dhokla.breaks.ui.theme

import androidx.compose.ui.graphics.Color

val LilacMist = Color(0xFFEFEAF7)
val Lilac = Color(0xFFE2D9F3)
val Periwinkle = Color(0xFFDEDDF2)
val InkViolet = Color(0xFF231C33)
val InkVioletSoft = Color(0xFF6F6880)
val Violet = Color(0xFF6750A4)
val VioletContainer = Color(0xFFE7DEFF)
val OnVioletContainer = Color(0xFF27194A)

val NightBase = Color(0xFF12101F)
val NightLift = Color(0xFF181429)
val NightGlowViolet = Color(0xFF2A2149)
val NightGlowIndigo = Color(0xFF221B3B)
val InkNight = Color(0xFFE8E4F5)
val InkNightSoft = Color(0xFF9E96BA)
val LavenderGlow = Color(0xFFC9BCF2)
val LavenderGlowContainer = Color(0xFF33295C)
val OnLavenderGlow = Color(0xFF1C1535)

data class CalmPalette(
    val gradient: List<Color>,
    val blobA: Color,
    val blobB: Color,
    val blobSurfaceA: Color,
    val blobSurfaceB: Color
)

val CalmLight = CalmPalette(
    gradient = listOf(
        Color(0xFFFCFAFE),
        Color(0xFFF6F3FB),
        Color(0xFFEEEAF7),
        Color(0xFFE8E6F3)
    ),
    blobA = Color(0xFFC9BAEF).copy(alpha = 0.60f),
    blobB = Color(0xFFBFC5EF).copy(alpha = 0.55f),
    blobSurfaceA = Color(0xFFD7CBF7),
    blobSurfaceB = Color(0xFFC3BBEE)
)

val CalmDark = CalmPalette(
    gradient = listOf(NightBase, NightLift, NightGlowIndigo),
    blobA = NightGlowViolet.copy(alpha = 0.75f),
    blobB = NightGlowIndigo.copy(alpha = 0.65f),
    blobSurfaceA = Color(0xFF332A5C),
    blobSurfaceB = Color(0xFF282048)
)
