package com.dhokla.breaks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dhokla.breaks.schedule.Scheduler
import com.dhokla.breaks.ui.HomeScreen
import com.dhokla.breaks.ui.OnboardingScreen
import com.dhokla.breaks.ui.SettingsScreen
import com.dhokla.breaks.ui.theme.BreaksTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BreaksTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val store = remember { context.applicationContext.breaksStore }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val prefs by store.prefs.collectAsState(initial = null)
    var showSettings by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch { Scheduler.healIfStalled(context, store) }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler(enabled = showSettings) { showSettings = false }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        val current = prefs
        when {
            current == null -> Unit
            !current.onboarded -> OnboardingScreen(
                onComplete = {
                    scope.launch {
                        Scheduler.startFirstBreak(context, store)
                        store.setOnboarded()
                    }
                }
            )
            else -> AnimatedContent(
                targetState = showSettings,
                transitionSpec = {
                    if (targetState) {
                        (
                            slideInHorizontally(tween(260)) { it / 6 } +
                                fadeIn(tween(260))
                            ) togetherWith (
                            slideOutHorizontally(tween(200)) { -it / 10 } +
                                fadeOut(tween(160))
                            )
                    } else {
                        (
                            slideInHorizontally(tween(260)) { -it / 6 } +
                                fadeIn(tween(260))
                            ) togetherWith (
                            slideOutHorizontally(tween(200)) { it / 10 } +
                                fadeOut(tween(160))
                            )
                    }
                },
                label = "rootNav"
            ) { isSettings ->
                if (isSettings) {
                    SettingsScreen(
                        prefs = current,
                        onBack = { showSettings = false },
                        onIntervalChange = { minutes ->
                            scope.launch { Scheduler.changeInterval(context, store, minutes) }
                        },
                        onStyleChange = { style -> scope.launch { store.setStyle(style) } },
                        onSoundChange = { enabled -> scope.launch { store.setSoundEnabled(enabled) } }
                    )
                } else {
                    HomeScreen(
                        prefs = current,
                        onOpenSettings = { showSettings = true }
                    )
                }
            }
        }
    }
}
