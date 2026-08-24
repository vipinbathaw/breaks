package com.dhokla.breaks.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dhokla.breaks.breaksStore
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
                Notifications.postBreakReminder(appContext, prefs.style, prefs.soundEnabled)
                Scheduler.startFirstBreak(appContext, appContext.breaksStore)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
