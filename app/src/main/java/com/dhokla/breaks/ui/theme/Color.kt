package com.dhokla.breaks.ui.theme

import androidx.compose.ui.graphics.Color

val Cream = Color(0xFFFAF8F3)
val CreamDeep = Color(0xFFF2F1EA)
val Mist = Color(0xFFE4ECF0)
val Sage = Color(0xFFDCE9DD)
val Ink = Color(0xFF22271F)
val InkSoft = Color(0xFF6C7265)
val Forest = Color(0xFF3F6B51)
val ForestContainer = Color(0xFFE0ECE2)
val OnForest = Color(0xFF1B2E22)

val NightBase = Color(0xFF111512)
val NightLift = Color(0xFF171D19)
val NightGlowGreen = Color(0xFF20362B)
val NightGlowBlue = Color(0xFF182A33)
val InkNight = Color(0xFFE7EBE1)
val InkNightSoft = Color(0xFF9BA498)
val Fern = Color(0xFFA7CBAD)
val FernContainer = Color(0xFF23342A)
val OnFern = Color(0xFF14231A)

data class CalmPalette(
    val top: Color,
    val bottom: Color,
    val blobA: Color,
    val blobB: Color
)

val CalmLight = CalmPalette(
    top = Cream,
    bottom = Color(0xFFEFF2EC),
    blobA = Sage.copy(alpha = 0.55f),
    blobB = Mist.copy(alpha = 0.50f)
)

val CalmDark = CalmPalette(
    top = NightBase,
    bottom = NightLift,
    blobA = NightGlowGreen.copy(alpha = 0.75f),
    blobB = NightGlowBlue.copy(alpha = 0.65f)
)
