package com.example.model

import com.example.R
import java.text.NumberFormat
import java.util.Locale

enum class AspectRatio(val label: String, val ratioFloat: Float, val iconLabel: String) {
  RATIO_16_9("16:9 Panorámico", 16f / 9f, "16:9"),
  RATIO_9_16("9:16 Shorts/Reels", 9f / 16f, "9:16"),
  RATIO_1_1("1:1 Cuadrado", 1f, "1:1"),
  RATIO_4_3("4:3 Clásico", 4f / 3f, "4:3")
}

enum class CameraMotion(val label: String, val description: String) {
  ZOOM_IN("Zoom In Dinámico", "Acercamiento cinemático continuo con foco en el sujeto"),
  PAN_HORIZONTAL("Barrido Panorámico", "Movimiento suave horizontal de izquierda a derecha"),
  ORBIT_TILT("Inclinación Orbital", "Ángulo contrapicado con rotación 3D tridimensional"),
  PARALLAX_DRIFT("Deriva Parallax", "Efecto de capas en profundidad con movimiento flotante"),
  STATIC_CINEMATIC("Cámara Fija de Autor", "Encuadre estático con vibración óptica sutil")
}

enum class AudioTheme(val label: String, val mood: String, val baseFreq: Float) {
  SCI_FI_SYNTH("Sintetizador Sci-Fi", "Pulsos electrónicos futuristas y pads envolventes", 220f),
  EPIC_ORCHESTRAL("Orquestal Épico", "Acordes majestuosos de cuerdas y timbales dramáticos", 174f),
  AMBIENT_RAIN("Lluvia & Neón", "Frecuencias relajantes de lluvia con texturas lo-fi", 261.63f),
  ETHEREAL_COSMIC("Cosmos Etéreo", "Frecuencia 432Hz espacial profunda y resonancia estelar", 216f),
  CYBERPUNK_BEAT("Ritmo Cyberpunk", "Línea de bajo sintetizado y arpegio rítmico", 130.81f)
}

data class SelectedImage(
  val id: String,
  val uriString: String? = null,
  val drawableResId: Int? = null,
  val title: String,
  val aspectRatio: AspectRatio = AspectRatio.RATIO_16_9
) {
  companion object {
    val presets = listOf(
      SelectedImage(
        id = "cyberpunk",
        drawableResId = R.drawable.sample_cyberpunk_city_1791413560967,
        title = "Metrópolis Cyberpunk",
        aspectRatio = AspectRatio.RATIO_16_9
      ),
      SelectedImage(
        id = "waterfall",
        drawableResId = R.drawable.sample_nature_waterfall_1791413573197,
        title = "Cascada Bioluminiscente",
        aspectRatio = AspectRatio.RATIO_16_9
      ),
      SelectedImage(
        id = "space",
        drawableResId = R.drawable.sample_space_nebula_1791413583603,
        title = "Nebulosa Cósmica",
        aspectRatio = AspectRatio.RATIO_16_9
      )
    )
  }
}

data class GeneratedVideo(
  val id: String,
  val title: String,
  val prompt: String,
  val enhancedPrompt: String,
  val imageUriString: String?,
  val imageResId: Int?,
  val durationMinutes: Long,
  val cameraMotion: CameraMotion,
  val audioTheme: AudioTheme,
  val soundEnabled: Boolean = true,
  val narratorVoice: String? = null,
  val narratorScript: String? = null,
  val createdAt: Long = System.currentTimeMillis()
)

object DurationFormatter {
  const val MAX_MINUTES: Long = 1_000_000_000L // 1,000,000,000 minutos

  fun formatMinutesToHumanReadable(minutes: Long): String {
    val numFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    return when {
      minutes < 1 -> "30 segundos"
      minutes == 1L -> "1 minuto"
      minutes < 60 -> "$minutes minutos"
      minutes < 1440 -> {
        val hours = minutes / 60
        val remMin = minutes % 60
        if (remMin == 0L) "$hours horas" else "$hours h $remMin min"
      }
      minutes < 525600 -> {
        val days = minutes / 1440
        val remHours = (minutes % 1440) / 60
        "$days días ($remHours h)"
      }
      else -> {
        // More than a year (525,600 min in a year)
        val years = minutes.toDouble() / 525600.0
        val formattedYears = String.format(Locale.US, "%.1f", years)
        "${numFormat.format(minutes)} min (~$formattedYears años)"
      }
    }
  }

  fun formatPlaybackTime(seconds: Long): String {
    val hours = seconds / 3600
    val remSec = seconds % 3600
    val minutes = remSec / 60
    val sec = remSec % 60
    return if (hours > 0) {
      String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, sec)
    } else {
      String.format(Locale.US, "%02d:%02d", minutes, sec)
    }
  }
}
