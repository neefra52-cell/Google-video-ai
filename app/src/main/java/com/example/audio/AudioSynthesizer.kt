package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import com.example.model.AudioTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class AudioSynthesizer(context: Context) {
  private val sampleRate = 44100
  private var audioTrack: AudioTrack? = null
  private var synthJob: Job? = null
  private var isPlaying = false
  private var currentVolume = 0.8f

  private val _visualizerAmplitudes = MutableStateFlow(FloatArray(16) { 0.1f })
  val visualizerAmplitudes: StateFlow<FloatArray> = _visualizerAmplitudes.asStateFlow()

  private var textToSpeech: TextToSpeech? = null
  private var isTtsReady = false

  init {
    try {
      textToSpeech = TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
          isTtsReady = true
          textToSpeech?.language = Locale.forLanguageTag("es-ES")
        }
      }
    } catch (_: Exception) {
      isTtsReady = false
    }
  }

  fun setVolume(volume: Float) {
    currentVolume = volume.coerceIn(0f, 1f)
    try {
      audioTrack?.setVolume(currentVolume)
    } catch (_: Exception) {}
  }

  fun playSound(
    scope: CoroutineScope,
    theme: AudioTheme,
    narratorScript: String? = null,
    enableNarrator: Boolean = false
  ) {
    stopSound()
    isPlaying = true

    // Speak narrator text if available
    if (enableNarrator && !narratorScript.isNullOrBlank() && isTtsReady) {
      try {
        textToSpeech?.speak(narratorScript, TextToSpeech.QUEUE_FLUSH, null, "veo_narrator")
      } catch (_: Exception) {}
    }

    synthJob = scope.launch(Dispatchers.Default) {
      val minBufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      )
      val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

      try {
        audioTrack = AudioTrack.Builder()
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

        audioTrack?.setVolume(currentVolume)
        audioTrack?.play()
      } catch (e: Exception) {
        // Fallback simulation mode
      }

      val buffer = ShortArray(bufferSize)
      var phase1 = 0.0
      var phase2 = 0.0
      var phase3 = 0.0
      var lfoPhase = 0.0
      var stepCounter = 0
      val baseFreq = theme.baseFreq.toDouble()

      val amps = FloatArray(16)

      while (isActive && isPlaying) {
        for (i in 0 until bufferSize) {
          val lfo = (sin(lfoPhase) + 1.0) * 0.5
          lfoPhase += 2.0 * PI * 0.25 / sampleRate

          val sampleVal: Double = when (theme) {
            AudioTheme.SCI_FI_SYNTH -> {
              val f1 = baseFreq * (1.0 + 0.05 * sin(lfoPhase * 2.0))
              val f2 = baseFreq * 1.5
              phase1 += 2.0 * PI * f1 / sampleRate
              phase2 += 2.0 * PI * f2 / sampleRate
              (sin(phase1) * 0.5 + sin(phase2) * 0.3) * (0.6 + 0.4 * lfo)
            }
            AudioTheme.EPIC_ORCHESTRAL -> {
              val f1 = baseFreq
              val f2 = baseFreq * 1.25 // Major third
              val f3 = baseFreq * 1.5  // Fifth
              phase1 += 2.0 * PI * f1 / sampleRate
              phase2 += 2.0 * PI * f2 / sampleRate
              phase3 += 2.0 * PI * f3 / sampleRate
              (sin(phase1) * 0.4 + sin(phase2) * 0.3 + sin(phase3) * 0.3)
            }
            AudioTheme.AMBIENT_RAIN -> {
              val rainNoise = (Random.nextDouble() * 2.0 - 1.0) * 0.25
              phase1 += 2.0 * PI * baseFreq / sampleRate
              sin(phase1) * 0.3 + rainNoise
            }
            AudioTheme.ETHEREAL_COSMIC -> {
              val f1 = 216.0 // 432 Hz sub-harmonic
              val f2 = 216.0 * 2.005 // slight detune for shimmering binaural beat
              phase1 += 2.0 * PI * f1 / sampleRate
              phase2 += 2.0 * PI * f2 / sampleRate
              (sin(phase1) * 0.45 + sin(phase2) * 0.45)
            }
            AudioTheme.CYBERPUNK_BEAT -> {
              val step = (stepCounter / (sampleRate / 4)) % 4
              val noteFreq = when (step) {
                0 -> baseFreq
                1 -> baseFreq * 1.122
                2 -> baseFreq * 1.335
                else -> baseFreq * 1.5
              }
              stepCounter++
              phase1 += 2.0 * PI * noteFreq / sampleRate
              // Sawtooth approximation
              val saw = (2.0 * (phase1 / (2.0 * PI) % 1.0) - 1.0) * 0.4
              saw + sin(phase1 * 0.5) * 0.3
            }
          }

          val clamped = (sampleVal * currentVolume * Short.MAX_VALUE).toInt()
            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
          buffer[i] = clamped.toShort()
        }

        try {
          audioTrack?.write(buffer, 0, bufferSize)
        } catch (_: Exception) {}

        // Calculate visualizer bars
        for (b in 0 until 16) {
          val idx = (b * bufferSize / 16).coerceIn(0, bufferSize - 1)
          val norm = (kotlin.math.abs(buffer[idx].toInt()).toFloat() / Short.MAX_VALUE).coerceIn(0.05f, 1.0f)
          amps[b] = amps[b] * 0.4f + norm * 0.6f
        }
        _visualizerAmplitudes.value = amps.copyOf()

        delay(16) // ~60fps updates
      }
    }
  }

  fun pauseSound() {
    isPlaying = false
    try {
      audioTrack?.pause()
    } catch (_: Exception) {}
    try {
      textToSpeech?.stop()
    } catch (_: Exception) {}
  }

  fun resumeSound() {
    isPlaying = true
    try {
      audioTrack?.play()
    } catch (_: Exception) {}
  }

  fun stopSound() {
    isPlaying = false
    synthJob?.cancel()
    synthJob = null
    try {
      audioTrack?.stop()
      audioTrack?.release()
    } catch (_: Exception) {}
    audioTrack = null
    try {
      textToSpeech?.stop()
    } catch (_: Exception) {}
    _visualizerAmplitudes.value = FloatArray(16) { 0.05f }
  }

  fun release() {
    stopSound()
    try {
      textToSpeech?.shutdown()
    } catch (_: Exception) {}
  }
}
