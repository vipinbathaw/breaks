package com.dhokla.breaks

import android.app.Application
import android.content.Context
import com.dhokla.breaks.data.BreaksStore
import com.dhokla.breaks.notify.Notifications

class BreaksApp : Application() {

    val store: BreaksStore by lazy { BreaksStore(this) }

    override fun onCreate() {
        super.onCreate()
        Notifications.ensureChannels(this)
    }
}

val Context.breaksStore: BreaksStore
    get() = (applicationContext as BreaksApp).store
