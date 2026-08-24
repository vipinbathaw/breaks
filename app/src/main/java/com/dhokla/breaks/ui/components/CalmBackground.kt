package com.dhokla.breaks.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.dhokla.breaks.ui.theme.LocalCalm
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CalmBackground(modifier: Modifier = Modifier) {
    val calm = LocalCalm.current
    val transition = rememberInfiniteTransition(label = "calm")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 42_000, easing = LinearEasing)
        ),
        label = "drift"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    Brush.verticalGradient(listOf(calm.top, calm.bottom))
                )
                val radius = size.minDimension * 0.95f
                val angle = t * 2f * Math.PI.toFloat()
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(calm.blobA, calm.blobA.copy(alpha = 0f)),
                        center = Offset(
                            x = size.width * (0.28f + 0.09f * sin(angle)),
                            y = size.height * (0.24f + 0.05f * cos(angle))
                        ),
                        radius = radius
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(calm.blobB, calm.blobB.copy(alpha = 0f)),
                        center = Offset(
                            x = size.width * (0.74f + 0.07f * cos(angle + 1.6f)),
                            y = size.height * (0.72f + 0.06f * sin(angle + 1.6f))
                        ),
                        radius = radius
                    )
                )
            }
    )
}
