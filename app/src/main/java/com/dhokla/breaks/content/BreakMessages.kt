package com.dhokla.breaks.content

object BreakMessages {

    private val messages = listOf(
        "Step away for a moment. Your brain will thank you.",
        "A short walk now keeps stiffness away later.",
        "Your legs carried you all day — give them a stretch.",
        "Stand up, roll those shoulders, take five easy steps.",
        "Two minutes of walking beats zero minutes of scrolling.",
        "20-20-20: look 20 feet away for 20 seconds.",
        "Screens make you blink less. Blink on purpose.",
        "Look out a window. Let your eyes focus on something far.",
        "Your eyes worked hard. Rest them on the horizon.",
        "Shoulders down, jaw unclenched, spine tall. There you go.",
        "Unclench your jaw. Drop your shoulders. Thank me later.",
        "Four counts in, six counts out. Repeat until calm.",
        "One deep breath is a reset button. Take three.",
        "Breathe in slowly, out slower. That's the whole exercise.",
        "If you're reading this, it's been a while since water.",
        "A glass of water now is easier than a headache later.",
        "Hydration check. You know what to do.",
        "Breaks boost focus more than pushing through does.",
        "Your brain ties ideas together during idle moments.",
        "Micro-breaks lower muscle strain and eye fatigue.",
        "Even a 40-second gaze break measurably relaxes your eyes.",
        "Standing up every half hour keeps your metabolism happier.",
        "The work will still be there. It always is. Go stretch.",
        "Plot twist: the break makes you faster afterwards.",
        "Your future self is watching. Look away from the screen.",
        "This notification is your official permission to relax.",
        "Stretch first, overthink after.",
        "Let your eyes rest on something farther than a screen.",
        "Slow down for sixty seconds. The world can wait.",
        "A quiet minute now makes a calmer hour later.",
        "Look up. Stretch. Breathe. That's it — that's the break."
    )

    private val bag = mutableListOf<String>()

    @Synchronized
    fun next(): String {
        if (bag.isEmpty()) {
            bag.addAll(messages.shuffled())
        }
        return bag.removeAt(bag.size - 1)
    }
}
