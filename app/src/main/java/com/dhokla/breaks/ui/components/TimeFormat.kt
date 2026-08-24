package com.dhokla.breaks.ui.components

fun formatTimeOfDay(minutesOfDay: Int): String {
    val hours = (minutesOfDay / 60) % 24
    val minutes = minutesOfDay % 60
    val amPm = if (hours < 12) "AM" else "PM"
    val hours12 = if (hours % 12 == 0) 12 else hours % 12
    return "$hours12:${minutes.toString().padStart(2, '0')} $amPm"
}
