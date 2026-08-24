package com.dhokla.breaks.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.dhokla.breaks.BreaksApp
import com.dhokla.breaks.notify.Notifications
import com.dhokla.breaks.schedule.Scheduler
import com.dhokla.breaks.ui.theme.BreaksTheme
import kotlinx.coroutines.launch

class BreakActivity : ComponentActivity() {

    private val store by lazy { (application as BreaksApp).store }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        enableEdgeToEdge()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    acknowledgeAndFinish()
                }
            }
        )

        val playSound = intent?.getBooleanExtra(EXTRA_PLAY_SOUND, false) ?: false
        setContent {
            BreaksTheme {
                BreakScreen(
                    playSound = playSound,
                    onAcknowledge = ::acknowledgeAndFinish
                )
            }
        }
    }

    private fun acknowledgeAndFinish() {
        lifecycleScope.launch {
            Scheduler.acknowledgeBreak(applicationContext, store)
            Notifications.cancelReminder(applicationContext)
            finish()
        }
    }

    companion object {
        const val EXTRA_PLAY_SOUND = "play_sound"
    }
}
