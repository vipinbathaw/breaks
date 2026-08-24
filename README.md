# Breaks

Dead simple Android app to remind you to take breaks.

Breaks sits quietly in the background and nudges you to step away at a healthy
interval — nothing more. No accounts, no tracking, no network access.

---

## Features

- **Countdown at a glance** — time until your next break, rendered inside a
  randomly generated organic blob that slowly rotates while you wait. Every
  launch gets a new shape.
- **Flexible intervals** — quick presets (15 / 20 / 30 / 45 / 60 min) or any
  custom value from 1 to 720 minutes.
- **Pause anytime** — a single tap silences Breaks until you're ready again.
  Paused state survives reboots and app restarts.
- **Active hours** — reminders only fire within a window of your choosing
  (default 9:00 AM – 9:00 PM). Overnight windows like 10 PM – 6 AM work too.
  Outside the window Breaks stays quiet and lines up the first reminder for
  the moment the window opens.
- **Two reminder styles** — a full-screen takeover when it matters, or a
  regular heads-up notification.
- **Gentle chime** — optional, with a soft vibration pattern to match.
- **Light, dark, or system theme** — a calm lavender palette in both modes,
  cycled from the home screen.

## Reliability

Breaks is built to actually fire, on phones that aggressively kill background
apps:

- Reminders are scheduled with [`AlarmManager.setAlarmClock()`](https://developer.android.com/reference/android/app/AlarmManager#setAlarmClock(android.app.AlarmManager.AlarmClockInfo,%20android.app.PendingIntent)),
  which OEMs treat as a user-facing alarm rather than deferrable background work.
- The reminder chain is self-healing: every fired reminder schedules the next
  one, so ignoring a notification never breaks the loop.
- The schedule is restored after device reboots and corrected whenever the app
  is opened.
- If the user ignores the exact-alarm or battery permissions, Breaks says so
  plainly and offers a one-tap fix instead of silently failing.

## Build

Requirements: Android Studio (or the Android SDK command-line tools) with JDK 17+.

```sh
git clone https://github.com/vipinbathaw/breaks.git
cd breaks
./gradlew assembleDebug
```

The debug APK lands in `app/build/outputs/apk/debug/app-debug.apk`.

To install on a connected device:

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Tech stack

- Kotlin
- Jetpack Compose + Material 3
- DataStore (preferences)
- AlarmManager (exact alarms)

## Privacy

Breaks collects nothing. No analytics, no crash reporting, no network
permissions — the app physically cannot phone home.

## License

[MIT](LICENSE) © Vipin Bathaw
