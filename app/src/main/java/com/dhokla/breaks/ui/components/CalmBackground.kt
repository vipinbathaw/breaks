package com.dhokla.breaks.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import com.dhokla.breaks.ui.theme.LocalCalm

@Composable
fun CalmBackground(modifier: Modifier = Modifier) {
    val calm = LocalCalm.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    Brush.verticalGradient(calm.gradient)
                )
            }
    )
}
