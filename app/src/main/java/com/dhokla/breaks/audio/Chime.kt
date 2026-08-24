package com.dhokla.breaks.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.sin

object Chime {

    private const val SAMPLE_RATE = 44_100

    fun play() {
        try {
            val totalMs = 1_300
            val sampleCount = SAMPLE_RATE * totalMs / 1_000
            val samples = ShortArray(sampleCount)

            val notes = listOf(783.99 to 0.0, 659.26 to 0.24)
            val noteDuration = 0.62

            for ((frequency, start) in notes) {
                val startSample = (start * SAMPLE_RATE).toInt()
                val length = (noteDuration * SAMPLE_RATE).toInt()
                for (i in 0 until length) {
                    val index = startSample + i
                    if (index >= sampleCount) break
                    val t = i.toDouble() / SAMPLE_RATE
                    val attack = min(1.0, t / 0.015)
                    val decay = exp(-t / 0.17)
                    val envelope = attack * decay
                    val value =
                        (sin(2 * PI * frequency * t) + 0.12 * sin(4 * PI * frequency * t)) *
                            envelope * 0.20
                    val existing = samples[index].toInt()
                    val combined = existing + (value * Short.MAX_VALUE).toInt()
                    samples[index] = combined.coerceIn(-32_768, 32_767).toShort()
                }
            }

            val fadeLength = SAMPLE_RATE * 80 / 1_000
            for (i in 0 until fadeLength) {
                val index = sampleCount - fadeLength + i
                val factor = 1f - i.toFloat() / fadeLength
                samples[index] = (samples[index] * factor).toInt().toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setTransferMode(AudioTrack.MODE_STATIC)
                .setBufferSizeInBytes(samples.size * 2)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    track.write(samples, 0, samples.size)
                    track.play()
                    delay(totalMs.toLong())
                } catch (_: Exception) {
                } finally {
                    runCatching { track.release() }
                }
            }
        } catch (_: Exception) {
        }
    }
}
