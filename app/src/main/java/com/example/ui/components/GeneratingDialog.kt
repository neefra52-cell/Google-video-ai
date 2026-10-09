package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratingDialog(
  step: Int,
  progress: Float,
  statusText: String
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "spin"
  )

  BasicAlertDialog(onDismissRequest = {}) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = SlateDark800),
      border = BorderStroke(1.5.dp, CyanNeon.copy(alpha = 0.6f)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Glowing spinning icon
        Box(
          modifier = Modifier
            .size(72.dp)
            .background(SlateDark900, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Generando",
            tint = CyanNeon,
            modifier = Modifier
              .size(36.dp)
              .rotate(rotation)
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
          text = "Google Video AI",
          color = TextPrimary,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Creando video cinemático con sonido...",
          color = AmberAccent,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Progress bar
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
          color = CyanNeon,
          trackColor = SlateDark900
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = statusText,
          color = TextSecondary,
          fontSize = 12.sp,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Steps list
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          GenerationStepItem(stepIndex = 1, currentStep = step, text = "1. Analizando imagen y texto")
          GenerationStepItem(stepIndex = 2, currentStep = step, text = "2. Animando movimiento de cámara")
          GenerationStepItem(stepIndex = 3, currentStep = step, text = "3. Sintetizando música y sonido")
          GenerationStepItem(stepIndex = 4, currentStep = step, text = "4. Finalizando video de Google Veo")
        }
      }
    }
  }
}

@Composable
private fun GenerationStepItem(
  stepIndex: Int,
  currentStep: Int,
  text: String
) {
  val isDone = currentStep > stepIndex
  val isCurrent = currentStep == stepIndex

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth()
  ) {
    if (isDone) {
      Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "Completado",
        tint = CyanNeon,
        modifier = Modifier.size(16.dp)
      )
    } else if (isCurrent) {
      Box(
        modifier = Modifier
          .size(16.dp)
          .background(AmberAccent, CircleShape)
      )
    } else {
      Box(
        modifier = Modifier
          .size(16.dp)
          .background(SlateDark900, CircleShape)
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Text(
      text = text,
      color = if (isDone || isCurrent) Color.White else TextSecondary,
      fontSize = 12.sp,
      fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
    )
  }
}
