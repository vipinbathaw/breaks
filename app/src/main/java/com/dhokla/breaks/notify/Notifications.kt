package com.dhokla.breaks.notify

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dhokla.breaks.R
import com.dhokla.breaks.data.ReminderStyle
import com.dhokla.breaks.ui.BreakActivity

object Notifications {

    const val REMINDER_NOTIFICATION_ID = 1001

    private const val CHANNEL_POPUP = "break_popup"
    private const val CHANNEL_NOTIFICATION = "break_notification"

    private const val PI_REQUEST_CONTENT = 1
    private const val PI_REQUEST_FULL_SCREEN = 2

    fun ensureChannels(context: Context) {
        val nm = NotificationManagerCompat.from(context)

        val popup = NotificationChannelCompat.Builder(
            CHANNEL_POPUP,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName(context.getString(R.string.channel_popup_name))
            .setVibrationPattern(longArrayOf(0, 45, 150, 45))
            .build()

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val soundAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val notification = NotificationChannelCompat.Builder(
            CHANNEL_NOTIFICATION,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName(context.getString(R.string.channel_notification_name))
            .setSound(soundUri, soundAttributes)
            .setVibrationPattern(longArrayOf(0, 60, 180, 60))
            .build()

        try {
            nm.createNotificationChannelsCompat(listOf(popup, notification))
        } catch (_: SecurityException) {
        }
    }

    fun canUseFullScreenIntent(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return true
        val nm = context.getSystemService(NotificationManager::class.java) ?: return false
        return nm.canUseFullScreenIntent()
    }

    fun notificationsEnabled(context: Context): Boolean =
        NotificationManagerCompat.from(context.applicationContext).areNotificationsEnabled()

    fun postBreakReminder(context: Context, style: ReminderStyle, soundEnabled: Boolean) {
        val appContext = context.applicationContext
        if (!notificationsEnabled(appContext)) return

        val wantsFullScreen = style == ReminderStyle.FULL_SCREEN
        val fullScreenAllowed = wantsFullScreen && canUseFullScreenIntent(appContext)

        val contentIntent = breakActivityPendingIntent(appContext, PI_REQUEST_CONTENT, false)
        val builder = NotificationCompat.Builder(
            appContext,
            if (fullScreenAllowed) CHANNEL_POPUP else CHANNEL_NOTIFICATION
        )
            .setSmallIcon(R.drawable.ic_stat_break)
            .setContentTitle(appContext.getString(R.string.notif_title))
            .setContentText(appContext.getString(R.string.notif_body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)

        if (fullScreenAllowed) {
            builder.setFullScreenIntent(
                breakActivityPendingIntent(appContext, PI_REQUEST_FULL_SCREEN, soundEnabled),
                true
            )
        } else {
            builder.setSilent(!soundEnabled)
        }

        try {
            NotificationManagerCompat.from(appContext).notify(
                REMINDER_NOTIFICATION_ID,
                builder.build()
            )
        } catch (_: SecurityException) {
        }
    }

    fun cancelReminder(context: Context) {
        NotificationManagerCompat.from(context.applicationContext).cancel(REMINDER_NOTIFICATION_ID)
    }

    private fun breakActivityPendingIntent(
        context: Context,
        requestCode: Int,
        playSound: Boolean
    ): PendingIntent {
        val intent = Intent(context, BreakActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(BreakActivity.EXTRA_PLAY_SOUND, playSound)
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
