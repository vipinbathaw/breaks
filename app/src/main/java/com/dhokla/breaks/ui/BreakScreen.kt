package com.dhokla.breaks.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dhokla.breaks.audio.Chime
import com.dhokla.breaks.ui.components.Blob
import com.dhokla.breaks.ui.components.CalmBackground

@Composable
fun BreakScreen(
    playSound: Boolean,
    onAcknowledge: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val entrance = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        if (playSound) Chime.play()
        entrance.animateTo(1f, tween(durationMillis = 460, easing = FastOutSlowInEasing))
    }

    val breathing = rememberInfiniteTransition(label = "breathe")
    val breatheScale by breathing.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5_200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        CalmBackground()
        Blob(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.88f)
                .graphicsLayer {
                    alpha = entrance.value
                    scaleX = breatheScale
                    scaleY = breatheScale
                    translationY = (1f - entrance.value) * 40f
                }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Time for a little break.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Step away for a moment.\nYour brain will thank you.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        Button(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onAcknowledge()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .fillMaxWidth(0.62f)
                .height(54.dp)
                .clip(CircleShape),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "OK", style = MaterialTheme.typography.labelLarge)
        }
    }
}
