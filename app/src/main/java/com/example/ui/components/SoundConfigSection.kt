package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioTheme
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark700
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SoundConfigSection(
  soundEnabled: Boolean,
  audioTheme: AudioTheme,
  volume: Float,
  narratorEnabled: Boolean,
  visualizerAmplitudes: FloatArray,
  onSoundToggle: (Boolean) -> Unit,
  onThemeSelected: (AudioTheme) -> Unit,
  onVolumeChanged: (Float) -> Unit,
  onNarratorToggle: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("sound_config_section"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = SlateDark800),
    border = BorderStroke(1.dp, if (soundEnabled) CyanNeon.copy(alpha = 0.5f) else SlateBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header with Master Sound Toggle
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Sonido AI",
            tint = CyanNeon,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "3. Diseño Sonoro AI",
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Text(
              text = if (soundEnabled) "Banda sonora y efectos activados" else "Video en silencio",
              color = if (soundEnabled) CyanNeon else TextSecondary,
              fontSize = 12.sp
            )
          }
        }

        Switch(
          checked = soundEnabled,
          onCheckedChange = onSoundToggle,
          colors = SwitchDefaults.colors(
            checkedThumbColor = SlateDark900,
            checkedTrackColor = CyanNeon,
            uncheckedTrackColor = SlateDark700
          ),
          modifier = Modifier.testTag("master_sound_switch")
        )
      }

      if (soundEnabled) {
        Spacer(modifier = Modifier.height(14.dp))

        // Audio Themes Chips
        Text(
          text = "Estilo Musical / Banda Sonora:",
          color = TextSecondary,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          AudioTheme.entries.forEach { theme ->
            val isSelected = (audioTheme == theme)
            FilterChip(
              selected = isSelected,
              onClick = { onThemeSelected(theme) },
              label = { Text(theme.label, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CyanNeon.copy(alpha = 0.2f),
                selectedLabelColor = CyanNeon,
                containerColor = SlateDark700,
                labelColor = Color.White
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isSelected,
                borderColor = SlateBorder,
                selectedBorderColor = CyanNeon
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "🎵 ${audioTheme.mood}",
          color = AmberAccent,
          fontSize = 11.sp,
          modifier = Modifier.padding(start = 2.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Narrator Voice Toggle
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SlateDark900,
          border = BorderStroke(1.dp, SlateBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voz Narrador",
                tint = AmberAccent,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "Narrador de Voz AI (Español)",
                  color = Color.White,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "Gemini TTS genera locución sincronizada",
                  color = TextSecondary,
                  fontSize = 11.sp
                )
              }
            }

            Switch(
              checked = narratorEnabled,
              onCheckedChange = onNarratorToggle,
              colors = SwitchDefaults.colors(
                checkedThumbColor = SlateDark900,
                checkedTrackColor = AmberAccent,
                uncheckedTrackColor = SlateDark700
              ),
              modifier = Modifier.testTag("narrator_voice_switch")
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Volume Slider & Visualizer Bars
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = "Volumen",
              tint = TextSecondary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Volumen (${(volume * 100).toInt()}%)",
              color = TextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }

          // Live audio visualizer minibar
          Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.height(16.dp)
          ) {
            visualizerAmplitudes.take(8).forEach { amp ->
              val barH = (amp * 16f).coerceIn(3f, 16f).dp
              Box(
                modifier = Modifier
                  .width(3.dp)
                  .height(barH)
                  .clip(RoundedCornerShape(1.dp))
                  .background(CyanNeon)
              )
            }
          }
        }

        Slider(
          value = volume,
          onValueChange = onVolumeChanged,
          colors = SliderDefaults.colors(
            thumbColor = CyanNeon,
            activeTrackColor = CyanNeon,
            inactiveTrackColor = SlateDark700
          ),
          modifier = Modifier.fillMaxWidth().testTag("sound_volume_slider")
        )
      }
    }
  }
}
