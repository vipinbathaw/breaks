package com.dhokla.breaks.schedule

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.dhokla.breaks.data.BreaksPrefs
import com.dhokla.breaks.data.BreaksStore
import com.dhokla.breaks.notify.ReminderReceiver
import java.util.Calendar

object Scheduler {

    private const val REMINDER_REQUEST_CODE = 421
    private const val SHOW_INTENT_REQUEST_CODE = 422
    private const val MISSED_GRACE_MS = 60_000L

    fun nextBreakAt(fromMs: Long, intervalMinutes: Int): Long =
        fromMs + intervalMinutes * 60_000L

    fun isWithinActiveWindow(prefs: BreaksPrefs, nowMs: Long): Boolean {
        val nowMinutes = minutesOfDay(nowMs)
        val start = prefs.activeStartMinutes
        val end = prefs.activeEndMinutes
        return when {
            start == end -> true
            start < end -> nowMinutes in start until end
            else -> nowMinutes >= start || nowMinutes < end
        }
    }

    fun nextWindowStartMs(prefs: BreaksPrefs, nowMs: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = nowMs
            set(Calendar.HOUR_OF_DAY, prefs.activeStartMinutes / 60)
            set(Calendar.MINUTE, prefs.activeStartMinutes % 60)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (cal.timeInMillis <= nowMs) cal.add(Calendar.DAY_OF_YEAR, 1)
        return cal.timeInMillis
    }

    private fun minutesOfDay(nowMs: Long): Int {
        val cal = Calendar.getInstance().apply { timeInMillis = nowMs }
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    }

    fun canScheduleExact(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val am = context.getSystemService(AlarmManager::class.java) ?: return false
        return am.canScheduleExactAlarms()
    }

    fun schedule(context: Context, atMs: Long) {
        val appContext = context.applicationContext
        val am = appContext.getSystemService(AlarmManager::class.java) ?: return
        val pi = reminderPendingIntent(appContext)
        am.cancel(pi)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMs, pi)
        } else {
            val showIntent = mainActivityPendingIntent(appContext)
            am.setAlarmClock(AlarmManager.AlarmClockInfo(atMs, showIntent), pi)
        }
    }

    fun cancel(context: Context) {
        val am = context.applicationContext.getSystemService(AlarmManager::class.java) ?: return
        am.cancel(reminderPendingIntent(context.applicationContext))
    }

    suspend fun startFirstBreak(context: Context, store: BreaksStore) {
        val prefs = store.snapshot()
        val at = nextBreakAt(System.currentTimeMillis(), prefs.intervalMinutes)
        store.setNextBreakAt(at)
        schedule(context, at)
    }

    suspend fun acknowledgeBreak(context: Context, store: BreaksStore) {
        startFirstBreak(context, store)
    }

    suspend fun changeInterval(context: Context, store: BreaksStore, minutes: Int) {
        val current = store.snapshot()
        if (minutes == current.intervalMinutes && current.nextBreakAt > 0L) {
            schedule(context, current.nextBreakAt)
            return
        }
        store.setInterval(minutes)
        val now = System.currentTimeMillis()
        val at = if (isWithinActiveWindow(current, now)) {
            nextBreakAt(now, minutes)
        } else {
            nextWindowStartMs(current, now)
        }
        store.setNextBreakAt(at)
        schedule(context, at)
    }

    suspend fun rescheduleForActiveHours(context: Context, store: BreaksStore) {
        val prefs = store.snapshot()
        if (!prefs.onboarded || !prefs.remindersEnabled) return
        val now = System.currentTimeMillis()
        val at = if (isWithinActiveWindow(prefs, now)) {
            val nb = prefs.nextBreakAt
            if (nb > now && isWithinActiveWindow(prefs, nb)) {
                nb
            } else {
                nextBreakAt(now, prefs.intervalMinutes)
            }
        } else {
            nextWindowStartMs(prefs, now)
        }
        store.setNextBreakAt(at)
        schedule(context, at)
    }

    suspend fun healIfStalled(context: Context, store: BreaksStore) {
        val prefs = store.snapshot()
        if (!prefs.onboarded || !prefs.remindersEnabled) return
        val now = System.currentTimeMillis()
        if (!isWithinActiveWindow(prefs, now)) {
            val at = nextWindowStartMs(prefs, now)
            store.setNextBreakAt(at)
            schedule(context, at)
            return
        }
        val at = when {
            prefs.nextBreakAt <= 0L -> nextBreakAt(now, prefs.intervalMinutes)
            now >= prefs.nextBreakAt + MISSED_GRACE_MS -> nextBreakAt(now, prefs.intervalMinutes)
            else -> {
                if (prefs.nextBreakAt > now) schedule(context, prefs.nextBreakAt)
                return
            }
        }
        store.setNextBreakAt(at)
        schedule(context, at)
    }

    private fun reminderPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun mainActivityPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, com.dhokla.breaks.MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            SHOW_INTENT_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
