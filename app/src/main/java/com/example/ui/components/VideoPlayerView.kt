package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.CameraMotion
import com.example.model.DurationFormatter
import com.example.model.GeneratedVideo
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextSecondary

@Composable
fun VideoPlayerView(
  video: GeneratedVideo?,
  isPlaying: Boolean,
  playbackPositionSeconds: Long,
  isMuted: Boolean,
  isFullscreen: Boolean,
  volume: Float,
  visualizerAmplitudes: FloatArray,
  onPlayPauseToggle: () -> Unit,
  onSeek: (Long) -> Unit,
  onToggleMute: () -> Unit,
  onToggleFullscreen: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (video == null) {
    Box(
      modifier = modifier
        .fillMaxWidth()
        .height(260.dp)
        .background(SlateDark900, RoundedCornerShape(16.dp)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "No hay ningún video cargado aún",
        color = TextSecondary,
        style = MaterialTheme.typography.bodyMedium
      )
    }
    return
  }

  val totalSeconds = (video.durationMinutes * 60L).coerceAtLeast(1L)
  val clampedPos = playbackPositionSeconds.coerceIn(0L, totalSeconds)

  // Camera motion animations
  val motionProgress = remember { Animatable(0f) }
  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      motionProgress.animateTo(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = 8000, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse
        )
      )
    }
  }

  // Particle shimmer animation
  val particleAnim = remember { Animatable(0f) }
  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      particleAnim.animateTo(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = 3000, easing = LinearEasing),
          repeatMode = RepeatMode.Restart
        )
      )
    }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("video_player_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = SlateDark900),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column {
      // Top header with title and badges
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Videocam,
            contentDescription = "Video",
            tint = CyanNeon,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = video.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            maxLines = 1
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CyanNeon.copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.4f))
        ) {
          Text(
            text = "Google Veo AI",
            color = CyanNeon,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // Video Viewport Canvas with Dynamic Camera Motion
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(16f / 9f)
          .clip(RoundedCornerShape(4.dp))
          .background(Color.Black)
      ) {
        // Image rendering with transformation
        val motionScale = when (video.cameraMotion) {
          CameraMotion.ZOOM_IN -> 1.0f + motionProgress.value * 0.18f
          CameraMotion.ORBIT_TILT -> 1.05f + motionProgress.value * 0.08f
          else -> 1.08f
        }
        val motionOffsetX = when (video.cameraMotion) {
          CameraMotion.PAN_HORIZONTAL -> (motionProgress.value - 0.5f) * 40f
          CameraMotion.PARALLAX_DRIFT -> (motionProgress.value - 0.5f) * 20f
          else -> 0f
        }
        val motionOffsetY = when (video.cameraMotion) {
          CameraMotion.ORBIT_TILT -> (motionProgress.value - 0.5f) * 20f
          CameraMotion.PARALLAX_DRIFT -> (motionProgress.value - 0.5f) * 15f
          else -> 0f
        }

        Box(
          modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
              scaleX = motionScale
              scaleY = motionScale
              translationX = motionOffsetX
              translationY = motionOffsetY
            }
        ) {
          if (!video.imageUriString.isNullOrBlank()) {
            AsyncImage(
              model = video.imageUriString,
              contentDescription = "Video frame",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          } else if (video.imageResId != null) {
            androidx.compose.foundation.Image(
              painter = painterResource(id = video.imageResId),
              contentDescription = "Video frame",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }

        // Particle & Shimmer canvas overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
          val width = size.width
          val height = size.height

          // Cinematic anamorphic lens flare beam
          val flareY = height * 0.35f
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(CyanNeon.copy(alpha = 0.25f), Color.Transparent),
              center = Offset(width * (0.2f + motionProgress.value * 0.6f), flareY),
              radius = width * 0.4f
            ),
            radius = width * 0.4f,
            center = Offset(width * (0.2f + motionProgress.value * 0.6f), flareY)
          )

          // Drifting cinematic sparks
          val p = particleAnim.value
          for (i in 0 until 12) {
            val sparkX = (width * ((i * 0.09f + p * 0.4f) % 1.0f))
            val sparkY = (height * ((i * 0.13f + (1f - p) * 0.3f) % 1.0f))
            drawCircle(
              color = AmberAccent.copy(alpha = 0.4f + (i % 3) * 0.2f),
              radius = 2.5f,
              center = Offset(sparkX, sparkY)
            )
          }
        }

        // Sound indicator badge overlay
        if (video.soundEnabled) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = SlateDark900.copy(alpha = 0.75f),
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(10.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Audiotrack,
                contentDescription = "Audio track",
                tint = AmberAccent,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = video.audioTheme.label,
                color = AmberAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        // Bottom real-time audio visualizer waveform overlay
        if (video.soundEnabled && isPlaying && !isMuted) {
          Box(
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .fillMaxWidth()
              .height(36.dp)
              .background(
                Brush.verticalGradient(
                  colors = listOf(Color.Transparent, SlateDark900.copy(alpha = 0.85f))
                )
              )
              .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.BottomCenter
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Bottom
            ) {
              visualizerAmplitudes.forEachIndexed { idx, amp ->
                val barHeight = (amp * 26f).coerceIn(4f, 26f).dp
                val barColor = if (idx % 2 == 0) CyanNeon else AmberAccent
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 1.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    .background(barColor.copy(alpha = 0.85f))
                )
              }
            }
          }
        }
      }

      // Playback Controls Bar
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(SlateDark800)
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        // Scrubbing Slider & Timestamps
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = DurationFormatter.formatPlaybackTime(clampedPos),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )

          Slider(
            value = clampedPos.toFloat(),
            onValueChange = { onSeek(it.toLong()) },
            valueRange = 0f..totalSeconds.toFloat(),
            colors = SliderDefaults.colors(
              thumbColor = CyanNeon,
              activeTrackColor = CyanNeon,
              inactiveTrackColor = SlateBorder
            ),
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 10.dp)
              .testTag("video_seek_slider")
          )

          Text(
            text = DurationFormatter.formatPlaybackTime(totalSeconds),
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        // Action Buttons Row: Rewind, Play/Pause, Forward, Mute, Fullscreen
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { onSeek((clampedPos - 10).coerceAtLeast(0L)) },
              modifier = Modifier.testTag("rewind_10s_button")
            ) {
              Icon(
                imageVector = Icons.Default.FastRewind,
                contentDescription = "Retroceder 10 segundos",
                tint = Color.White
              )
            }

            Surface(
              onClick = onPlayPauseToggle,
              shape = CircleShape,
              color = CyanNeon,
              modifier = Modifier
                .size(46.dp)
                .testTag("play_pause_button")
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                  tint = SlateDark900,
                  modifier = Modifier.size(28.dp)
                )
              }
            }

            IconButton(
              onClick = { onSeek((clampedPos + 10).coerceAtMost(totalSeconds)) },
              modifier = Modifier.testTag("forward_10s_button")
            ) {
              Icon(
                imageVector = Icons.Default.FastForward,
                contentDescription = "Adelantar 10 segundos",
                tint = Color.White
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onToggleMute,
              modifier = Modifier.testTag("toggle_mute_button")
            ) {
              Icon(
                imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = if (isMuted) "Activar sonido" else "Silenciar",
                tint = if (isMuted) AmberAccent else Color.White
              )
            }

            IconButton(
              onClick = onToggleFullscreen,
              modifier = Modifier.testTag("fullscreen_button")
            ) {
              Icon(
                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                contentDescription = "Pantalla completa",
                tint = Color.White
              )
            }
          }
        }

        // Narrator script subtitle card if present
        if (!video.narratorScript.isNullOrBlank()) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = SlateDark900.copy(alpha = 0.6f),
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "🎙️ Voz AI:",
                color = AmberAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = video.narratorScript,
                color = Color.White,
                fontSize = 12.sp,
                lineHeight = 16.sp
              )
            }
          }
        }
      }
    }
  }
}
