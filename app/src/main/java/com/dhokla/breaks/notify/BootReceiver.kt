package com.dhokla.breaks.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dhokla.breaks.breaksStore
import com.dhokla.breaks.schedule.Scheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.Default).launch {
            try {
                Scheduler.healIfStalled(appContext, appContext.breaksStore)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
