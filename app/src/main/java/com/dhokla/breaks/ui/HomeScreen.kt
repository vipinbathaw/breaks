package com.dhokla.breaks.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhokla.breaks.R
import com.dhokla.breaks.data.BreaksPrefs
import com.dhokla.breaks.data.ReminderStyle
import com.dhokla.breaks.data.ThemeMode
import com.dhokla.breaks.notify.Notifications
import com.dhokla.breaks.schedule.Scheduler
import com.dhokla.breaks.ui.components.Blob
import com.dhokla.breaks.ui.components.rememberResumeTick
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun HomeScreen(
    prefs: BreaksPrefs,
    themeMode: ThemeMode,
    onOpenSettings: () -> Unit,
    onCycleTheme: () -> Unit
) {
    val context = LocalContext.current
    val resumeTick = rememberResumeTick()

    var nowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (isActive) {
            nowMs = System.currentTimeMillis()
            delay(1_000 - nowMs % 1_000)
        }
    }

    val notificationsAllowed = remember(resumeTick) {
        Notifications.notificationsEnabled(context)
    }
    val exactAlarmsAllowed = remember(resumeTick) {
        Scheduler.canScheduleExact(context)
    }
    val fullScreenAllowed = remember(resumeTick) {
        Notifications.canUseFullScreenIntent(context)
    }
    var countdownScale by remember { mutableFloatStateOf(1f) }

    val remaining = prefs.nextBreakAt - nowMs
    val countdownText = formatRemaining(remaining)
    val label = if (remaining <= 0L) {
        "right about now"
    } else {
        "until your next break"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .semantics(mergeDescendants = true) {},
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Blob(modifier = Modifier.fillMaxWidth(0.84f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedContent(
                        targetState = countdownText,
                        transitionSpec = {
                            (
                                slideInVertically(tween(260)) { it / 5 } + fadeIn(tween(260))
                                ) togetherWith fadeOut(tween(160))
                        },
                        label = "countdown"
                    ) { text ->
                        val base = MaterialTheme.typography.displayMedium
                        Text(
                            text = text,
                            style = base.copy(
                                fontSize = (base.fontSize.value * countdownScale).sp,
                                lineHeight = (base.lineHeight.value * countdownScale).sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            softWrap = false,
                            maxLines = 1,
                            onTextLayout = { layout ->
                                if (layout.hasVisualOverflow) {
                                    countdownScale = (countdownScale - 0.06f).coerceAtLeast(0.5f)
                                }
                            }
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 28.dp)
                .padding(end = 56.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!notificationsAllowed) {
                NoticeLine(
                    text = "Notifications are off, so reminders can\u2019t reach you.",
                    actionLabel = "Enable",
                    onAction = { openAppNotificationSettings(context) }
                )
            }
            if (!exactAlarmsAllowed) {
                NoticeLine(
                    text = "Reminders may arrive a few minutes late.",
                    actionLabel = "Fix",
                    onAction = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            context.startActivity(
                                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                    .setData(Uri.parse("package:${context.packageName}"))
                            )
                        }
                    }
                )
            } else if (prefs.style == ReminderStyle.FULL_SCREEN &&
                !fullScreenAllowed
            ) {
                NoticeLine(
                    text = "Full-screen reminders aren\u2019t allowed right now.",
                    actionLabel = "Allow",
                    onAction = {
                        context.startActivity(
                            Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT)
                                .setData(Uri.parse("package:${context.packageName}"))
                        )
                    }
                )
            }
        }

        IconButton(
            onClick = onCycleTheme,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(
                    when (themeMode) {
                        ThemeMode.SYSTEM -> R.drawable.ic_theme_system
                        ThemeMode.LIGHT -> R.drawable.ic_theme_light
                        ThemeMode.DARK -> R.drawable.ic_theme_dark
                    }
                ),
                contentDescription = "Theme: ${themeMode.name.lowercase()}",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun NoticeLine(
    text: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f, fill = false)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = actionLabel,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(onClick = onAction)
                .padding(4.dp)
        )
    }
}

private fun openAppNotificationSettings(context: android.content.Context) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.parse("package:${context.packageName}"))
    }
    context.startActivity(intent)
}

private fun formatRemaining(msRemaining: Long): String {
    if (msRemaining <= 0L) return "0:00"
    val totalSeconds = (msRemaining + 999) / 1_000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
