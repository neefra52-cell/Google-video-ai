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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun PromptSection(
  promptText: String,
  isEnhancing: Boolean,
  isGenerating: Boolean,
  onPromptChanged: (String) -> Unit,
  onEnhanceWithAI: () -> Unit,
  onGenerateClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val quickTags = listOf(
    "🎬 Plano Secuencia 8K",
    "⚡ Slow Motion 120fps",
    "🌧️ Lluvia & Luces Neón",
    "🌌 Espacio Profundo",
    "✨ Niebla Volumétrica",
    "🔊 Dolby Atmos 3D"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("prompt_section"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = SlateDark800),
    border = BorderStroke(1.dp, SlateBorder)
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
            imageVector = Icons.Default.EditNote,
            contentDescription = "Prompt de texto",
            tint = CyanNeon,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "4. Escribir Texto para el Video",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        }

        OutlinedButton(
          onClick = onEnhanceWithAI,
          enabled = !isEnhancing && promptText.isNotBlank(),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = AmberAccent,
            disabledContentColor = TextSecondary
          ),
          border = BorderStroke(1.dp, if (promptText.isNotBlank()) AmberAccent else SlateBorder),
          modifier = Modifier.testTag("enhance_prompt_button")
        ) {
          if (isEnhancing) {
            CircularProgressIndicator(
              modifier = Modifier.size(14.dp),
              strokeWidth = 2.dp,
              color = AmberAccent
            )
          } else {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Mejorar con Gemini AI",
              modifier = Modifier.size(14.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Mejorar AI",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Input field
      OutlinedTextField(
        value = promptText,
        onValueChange = onPromptChanged,
        placeholder = {
          Text(
            text = "Describe la acción, escena o historia que deseas ver en el video AI...",
            color = TextSecondary,
            fontSize = 14.sp
          )
        },
        trailingIcon = {
          if (promptText.isNotEmpty()) {
            IconButton(onClick = { onPromptChanged("") }) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Limpiar texto",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        },
        minLines = 3,
        maxLines = 6,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = SlateDark900,
          unfocusedContainerColor = SlateDark900,
          focusedBorderColor = CyanNeon,
          unfocusedBorderColor = SlateBorder,
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("prompt_text_input")
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Quick Tag Chips
      Text(
        text = "Etiquetas de Estilo Rápido:",
        color = TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
      )
      Spacer(modifier = Modifier.height(6.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        quickTags.forEach { tag ->
          FilterChip(
            selected = promptText.contains(tag),
            onClick = {
              val newText = if (promptText.contains(tag)) {
                promptText.replace(", $tag", "").replace(tag, "").trim()
              } else {
                if (promptText.isBlank()) tag else "$promptText, $tag"
              }
              onPromptChanged(newText)
            },
            label = { Text(tag, fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = AmberAccent.copy(alpha = 0.25f),
              selectedLabelColor = AmberAccent,
              containerColor = SlateDark700,
              labelColor = Color.White
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = promptText.contains(tag),
              borderColor = SlateBorder,
              selectedBorderColor = AmberAccent
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Large Primary Generate Button
      Button(
        onClick = onGenerateClick,
        enabled = !isGenerating && promptText.isNotBlank(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = CyanNeon,
          contentColor = SlateDark900,
          disabledContainerColor = SlateDark700,
          disabledContentColor = TextSecondary
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("generate_video_button")
      ) {
        if (isGenerating) {
          CircularProgressIndicator(
            color = SlateDark900,
            modifier = Modifier.size(24.dp),
            strokeWidth = 3.dp
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Renderizando Video...",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        } else {
          Icon(
            imageVector = Icons.Default.MovieCreation,
            contentDescription = "Generar",
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Generar Video Google Veo AI con Sonido",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
