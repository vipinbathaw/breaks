package com.dhokla.breaks.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dhokla.breaks.breaksStore
import com.dhokla.breaks.content.BreakMessages
import com.dhokla.breaks.schedule.Scheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val prefs = appContext.breaksStore.snapshot()
                if (prefs.remindersEnabled) {
                    val now = System.currentTimeMillis()
                    if (Scheduler.isWithinActiveWindow(prefs, now)) {
                        Notifications.postBreakReminder(
                            appContext,
                            prefs.style,
                            prefs.soundEnabled,
                            BreakMessages.next()
                        )
                        Scheduler.startFirstBreak(appContext, appContext.breaksStore)
                    } else {
                        val at = Scheduler.nextWindowStartMs(prefs, now)
                        appContext.breaksStore.setNextBreakAt(at)
                        Scheduler.schedule(appContext, at)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
