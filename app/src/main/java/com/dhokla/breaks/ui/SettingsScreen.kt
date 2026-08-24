package com.dhokla.breaks.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dhokla.breaks.data.BreaksPrefs
import com.dhokla.breaks.data.BreaksStore
import com.dhokla.breaks.data.ReminderStyle
import com.dhokla.breaks.notify.Notifications
import com.dhokla.breaks.schedule.Scheduler

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    prefs: BreaksPrefs,
    onBack: () -> Unit,
    onIntervalChange: (Int) -> Unit,
    onStyleChange: (ReminderStyle) -> Unit,
    onSoundChange: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 40.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.height(24.dp))

        SectionLabel("Break interval")
        Spacer(Modifier.height(14.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BreaksStore.INTERVAL_PRESETS.forEach { minutes ->
                IntervalPill(
                    label = "$minutes min",
                    selected = minutes == prefs.intervalMinutes,
                    onClick = { onIntervalChange(minutes) }
                )
            }
        }

        Divider()
        SectionLabel("Reminder style")
        Spacer(Modifier.height(6.dp))
        StyleRow(
            title = "Full-screen reminder",
            subtitle = "Appears front and center",
            selected = prefs.style == ReminderStyle.FULL_SCREEN,
            onClick = { onStyleChange(ReminderStyle.FULL_SCREEN) }
        )
        StyleRow(
            title = "Normal notification",
            subtitle = "A regular heads-up notification",
            selected = prefs.style == ReminderStyle.NOTIFICATION,
            onClick = { onStyleChange(ReminderStyle.NOTIFICATION) }
        )
        if (prefs.style == ReminderStyle.FULL_SCREEN &&
            !Notifications.canUseFullScreenIntent(context)
        ) {
            HintLine(
                text = "Android isn\u2019t letting Breaks show full-screen reminders yet. " +
                    "You\u2019ll get a normal notification instead.",
                actionLabel = "Review",
                onAction = {
                    context.startActivity(
                        Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT)
                            .setData(Uri.parse("package:${context.packageName}"))
                    )
                }
            )
        }

        Divider()
        SectionLabel("Sound")
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Reminder sound",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "A short, gentle chime",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = prefs.soundEnabled,
                onCheckedChange = { onSoundChange(it) }
            )
        }

        if (!Scheduler.canScheduleExact(context)) {
            Divider()
            HintLine(
                text = "Precise alarms are off, so reminders may arrive a few minutes late.",
                actionLabel = "Allow",
                onAction = {
                    context.startActivity(
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            .setData(Uri.parse("package:${context.packageName}"))
                    )
                }
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun IntervalPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .heightIn(min = 44.dp)
            .clip(CircleShape)
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    Color.Transparent
                }
            )
            .then(
                if (selected) {
                    Modifier
                } else {
                    Modifier.border(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        shape = CircleShape
                    )
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.background
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun StyleRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        RadioButtonDot(selected = selected)
    }
}

@Composable
private fun RadioButtonDot(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(11.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun HintLine(
    text: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Column(Modifier.padding(top = 8.dp)) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = actionLabel,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(onClick = onAction)
                .padding(top = 6.dp)
        )
    }
}

@Composable
private fun Divider() {
    Spacer(Modifier.height(28.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
    Spacer(Modifier.height(24.dp))
}
