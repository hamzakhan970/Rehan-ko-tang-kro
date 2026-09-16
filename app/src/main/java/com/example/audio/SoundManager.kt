package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

class SoundManager(private val context: Context) {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  var soundEnabled: Boolean = true
  var hapticsEnabled: Boolean = true

  private val vibrator: Vibrator? by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      manager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  }

  fun vibrate(durationMs: Long = 40) {
    if (!hapticsEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(
          VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
        )
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (_: Exception) {}
  }

  fun vibratePattern(timings: LongArray) {
    if (!hapticsEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(timings, -1)
      }
    } catch (_: Exception) {}
  }

  fun playTap() {
    if (!soundEnabled) return
    vibrate(20)
    scope.launch {
      playTone(frequency = 520.0, durationMs = 35, type = WaveType.SINE)
    }
  }

  fun playBoing() {
    if (!soundEnabled) return
    vibrate(50)
    scope.launch {
      playFrequencySweep(startFreq = 220.0, endFreq = 750.0, durationMs = 220)
    }
  }

  fun playLaugh() {
    if (!soundEnabled) return
    vibratePattern(longArrayOf(0, 40, 50, 40, 50, 60))
    scope.launch {
      val pitches = doubleArrayOf(380.0, 450.0, 520.0, 440.0, 560.0)
      for (pitch in pitches) {
        playTone(pitch, durationMs = 65, type = WaveType.SINE)
      }
    }
  }

  fun playWhoopee() {
    if (!soundEnabled) return
    vibrate(80)
    scope.launch {
      playFrequencySweep(startFreq = 280.0, endFreq = 95.0, durationMs = 260)
    }
  }

  fun playCoin() {
    if (!soundEnabled) return
    vibrate(30)
    scope.launch {
      playTone(frequency = 987.77, durationMs = 60, type = WaveType.SINE) // B5
      playTone(frequency = 1318.51, durationMs = 120, type = WaveType.SINE) // E6
    }
  }

  fun playAlert() {
    if (!soundEnabled) return
    vibratePattern(longArrayOf(0, 100, 60, 100))
    scope.launch {
      playTone(frequency = 780.0, durationMs = 80, type = WaveType.SQUARE)
      playTone(frequency = 880.0, durationMs = 100, type = WaveType.SQUARE)
    }
  }

  fun playCaught() {
    if (!soundEnabled) return
    vibratePattern(longArrayOf(0, 150, 80, 200))
    scope.launch {
      // Comic wah-wah horn
      val notes = doubleArrayOf(350.0, 330.0, 310.0, 260.0)
      for (note in notes) {
        playTone(frequency = note, durationMs = 140, type = WaveType.SAWTOOTH)
      }
    }
  }

  fun playPrankVictory() {
    if (!soundEnabled) return
    vibratePattern(longArrayOf(0, 60, 40, 60, 40, 120))
    scope.launch {
      val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50) // C5 - E5 - G5 - C6
      for (note in notes) {
        playTone(frequency = note, durationMs = 110, type = WaveType.SINE)
      }
    }
  }

  fun playSplat() {
    if (!soundEnabled) return
    vibrate(70)
    scope.launch {
      playNoiseBurst(durationMs = 130)
    }
  }

  fun playAlarm() {
    if (!soundEnabled) return
    vibratePattern(longArrayOf(0, 100, 50, 100, 50, 150))
    scope.launch {
      for (i in 0 until 4) {
        playTone(frequency = 880.0, durationMs = 60, type = WaveType.SQUARE)
        playTone(frequency = 1100.0, durationMs = 80, type = WaveType.SQUARE)
      }
    }
  }

  fun playSneakStep() {
    if (!soundEnabled) return
    vibrate(15)
    scope.launch {
      playTone(frequency = 240.0 + Random.nextInt(40), durationMs = 30, type = WaveType.SINE)
    }
  }

  private enum class WaveType {
    SINE, SQUARE, SAWTOOTH
  }

  private fun playTone(frequency: Double, durationMs: Int, type: WaveType) {
    val sampleRate = 22050
    val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
    val buffer = ShortArray(numSamples)

    val angularFreq = 2.0 * Math.PI * frequency / sampleRate

    for (i in 0 until numSamples) {
      // Envelope to avoid audio clicking (fade in and fade out)
      val progress = i.toDouble() / numSamples
      val envelope = when {
        progress < 0.1 -> progress / 0.1
        progress > 0.8 -> (1.0 - progress) / 0.2
        else -> 1.0
      }

      val rawSample = when (type) {
        WaveType.SINE -> sin(angularFreq * i)
        WaveType.SQUARE -> if (sin(angularFreq * i) >= 0) 0.6 else -0.6
        WaveType.SAWTOOTH -> (2.0 * ((i * frequency / sampleRate) % 1.0)) - 1.0
      }

      buffer[i] = (rawSample * envelope * Short.MAX_VALUE * 0.45).toInt().toShort()
    }

    playShortBuffer(buffer, sampleRate)
  }

  private fun playFrequencySweep(startFreq: Double, endFreq: Double, durationMs: Int) {
    val sampleRate = 22050
    val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
    val buffer = ShortArray(numSamples)

    var phase = 0.0
    for (i in 0 until numSamples) {
      val progress = i.toDouble() / numSamples
      val currentFreq = startFreq + (endFreq - startFreq) * progress
      phase += 2.0 * Math.PI * currentFreq / sampleRate

      val envelope = when {
        progress < 0.08 -> progress / 0.08
        progress > 0.85 -> (1.0 - progress) / 0.15
        else -> 1.0
      }

      buffer[i] = (sin(phase) * envelope * Short.MAX_VALUE * 0.5).toInt().toShort()
    }

    playShortBuffer(buffer, sampleRate)
  }

  private fun playNoiseBurst(durationMs: Int) {
    val sampleRate = 22050
    val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
    val buffer = ShortArray(numSamples)

    for (i in 0 until numSamples) {
      val progress = i.toDouble() / numSamples
      val envelope = (1.0 - progress) * (1.0 - progress)
      val noise = (Random.nextDouble() * 2.0 - 1.0)
      buffer[i] = (noise * envelope * Short.MAX_VALUE * 0.4).toInt().toShort()
    }

    playShortBuffer(buffer, sampleRate)
  }

  private fun playShortBuffer(buffer: ShortArray, sampleRate: Int) {
    try {
      val audioTrack = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
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

      audioTrack.write(buffer, 0, buffer.size)
      audioTrack.play()

      // Allow playback then release
      Thread.sleep((buffer.size * 1000L / sampleRate).coerceAtLeast(20))
      audioTrack.stop()
      audioTrack.release()
    } catch (_: Exception) {}
  }
}
