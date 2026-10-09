package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DurationFormatter
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark700
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DurationSelectorSection(
  currentMinutes: Long,
  onDurationSelected: (Long) -> Unit,
  onOpenCustomDurationDialog: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isMaxLimit = (currentMinutes == DurationFormatter.MAX_MINUTES)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("duration_selector_section"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = SlateDark800),
    border = BorderStroke(1.dp, if (isMaxLimit) AmberAccent else SlateBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = "Duración",
            tint = AmberAccent,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "2. Duración del Video",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = AmberAccent.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f))
        ) {
          Text(
            text = "Máx: 1,000,000,000 min",
            color = AmberAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Current Selected Duration Display Banner
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = SlateDark900,
        border = BorderStroke(1.dp, if (isMaxLimit) OrangeFlame else SlateBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Duración Seleccionada:",
              color = TextSecondary,
              fontSize = 11.sp
            )
            Text(
              text = DurationFormatter.formatMinutesToHumanReadable(currentMinutes),
              color = if (isMaxLimit) OrangeFlame else CyanNeon,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 16.sp
            )
          }

          OutlinedButton(
            onClick = onOpenCustomDurationDialog,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier.testTag("custom_duration_button")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Editar duración",
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Personalizar", fontSize = 12.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Quick Preset Chips
      Text(
        text = "Accesos Rápidos de Duración:",
        color = TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(6.dp))

      val presets = listOf(
        1L to "1 min",
        5L to "5 min",
        15L to "15 min",
        60L to "1 hora",
        1440L to "24 horas",
        DurationFormatter.MAX_MINUTES to "1,000,000,000 min (Máximo)"
      )

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        presets.forEach { (minutes, label) ->
          val isSelected = (currentMinutes == minutes)
          val isUltra = (minutes == DurationFormatter.MAX_MINUTES)
          FilterChip(
            selected = isSelected,
            onClick = { onDurationSelected(minutes) },
            leadingIcon = {
              if (isUltra) {
                Icon(
                  imageVector = Icons.Default.AllInclusive,
                  contentDescription = "Infinito",
                  tint = if (isSelected) OrangeFlame else AmberAccent,
                  modifier = Modifier.size(16.dp)
                )
              } else {
                Icon(
                  imageVector = Icons.Default.Timer,
                  contentDescription = "Tiempo",
                  tint = if (isSelected) CyanNeon else TextSecondary,
                  modifier = Modifier.size(14.dp)
                )
              }
            },
            label = {
              Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isUltra) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = if (isUltra) OrangeFlame.copy(alpha = 0.25f) else CyanNeon.copy(alpha = 0.2f),
              selectedLabelColor = if (isUltra) OrangeFlame else CyanNeon,
              containerColor = SlateDark700,
              labelColor = Color.White
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = if (isUltra) OrangeFlame.copy(alpha = 0.5f) else SlateBorder,
              selectedBorderColor = if (isUltra) OrangeFlame else CyanNeon
            )
          )
        }
      }

      // Clarifying tech footnote for massive durations
      if (currentMinutes > 1440L) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = OrangeFlame.copy(alpha = 0.1f),
          border = BorderStroke(1.dp, OrangeFlame.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "⚡ Modo Continuo Veo AI: Genera un bucle dinámico procedural infinito con transiciones continuas sin pérdida de resolución para cubrir la duración máxima de 10^9 minutos.",
            color = AmberAccent,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            modifier = Modifier.padding(10.dp)
          )
        }
      }
    }
  }
}
