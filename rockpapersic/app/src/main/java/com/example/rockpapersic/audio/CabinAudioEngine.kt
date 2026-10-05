package com.example.rockpapersic.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.sin

class CabinAudioEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var isEngineNoisePlaying = false
    private var engineNoiseThread: Thread? = null

    var isVoiceEnabled: Boolean = true
    var isEngineNoiseEnabled: Boolean = false
        set(value) {
            field = value
            if (value) startEngineNoise() else stopEngineNoise()
        }

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.TAIWAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.CHINESE)
            }
            tts?.setSpeechRate(0.95f) // Slightly relaxed cabin announcement pace
            tts?.setPitch(1.05f) // Warm clear cabin voice
            isTtsReady = true
        }
    }

    fun speakAnnouncement(text: String, playChimeBefore: Boolean = true) {
        if (!isVoiceEnabled) return
        thread {
            if (playChimeBefore) {
                playCabinChime()
                Thread.sleep(600)
            }
            if (isTtsReady) {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "announcement_${System.currentTimeMillis()}")
            }
        }
    }

    fun playCabinChime() {
        thread {
            try {
                val sampleRate = 44100
                val durationMs = 1200
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                // High tone (F5 - 698 Hz) for first 400ms, then Low tone (C5 - 523 Hz)
                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val freq = if (time < 0.4) 698.46 else 523.25
                    val envelope = if (time < 0.4) {
                        Math.exp(-time * 5.0)
                    } else {
                        Math.exp(-(time - 0.4) * 4.0)
                    }
                    val sample = (sin(2.0 * Math.PI * freq * time) * envelope * 20000).toInt()
                    buffer[i] = sample.coerceIn(-32768, 32767).toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                Thread.sleep(durationMs.toLong())
                track.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startEngineNoise() {
        if (isEngineNoisePlaying) return
        isEngineNoisePlaying = true
        engineNoiseThread = thread {
            try {
                val sampleRate = 22050
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = maxOf(minBufferSize, 4096)
                val buffer = ShortArray(bufferSize)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()

                var lastSample = 0.0
                while (isEngineNoisePlaying) {
                    // Brownian / Low-pass filtered pink-white noise to simulate airplane cabin hum
                    for (i in buffer.indices) {
                        val white = (Math.random() * 2.0 - 1.0)
                        lastSample = (lastSample + (0.02 * white)) / 1.02
                        val sample = (lastSample * 4000.0).toInt()
                        buffer[i] = sample.coerceIn(-32768, 32767).toShort()
                    }
                    track.write(buffer, 0, buffer.size)
                }

                track.stop()
                track.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun stopEngineNoise() {
        isEngineNoisePlaying = false
        engineNoiseThread?.interrupt()
        engineNoiseThread = null
    }

    fun release() {
        stopEngineNoise()
        tts?.stop()
        tts?.shutdown()
    }
}
