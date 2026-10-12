package com.example.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundManager(private val context: Context) {

  private var toneGenerator: ToneGenerator? = null
  private val vibrator: Vibrator? by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vibratorManager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  }

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
    } catch (_: Exception) {
      toneGenerator = null
    }
  }

  fun playDiceRollSound(soundEnabled: Boolean) {
    if (!soundEnabled) return
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
        delay(90)
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
        delay(90)
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 100)
      } catch (_: Exception) {}
    }
  }

  fun playMoveSound(soundEnabled: Boolean) {
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 50)
    } catch (_: Exception) {}
  }

  fun playCaptureSound(soundEnabled: Boolean) {
    if (!soundEnabled) return
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 120)
        delay(130)
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 180)
      } catch (_: Exception) {}
    }
  }

  fun playWinSound(soundEnabled: Boolean) {
    if (!soundEnabled) return
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 150)
        delay(160)
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 250)
      } catch (_: Exception) {}
    }
  }

  fun vibrate(vibrationEnabled: Boolean, durationMs: Long = 40) {
    if (!vibrationEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (_: Exception) {}
  }
}
