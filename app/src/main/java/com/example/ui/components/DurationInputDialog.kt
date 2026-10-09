package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DurationFormatter
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DurationInputDialog(
  initialMinutes: Long,
  onDismiss: () -> Unit,
  onConfirm: (Long) -> Unit
) {
  var textInput by remember { mutableStateOf(initialMinutes.toString()) }
  val currentVal = textInput.toLongOrNull() ?: 1L
  val isValid = currentVal in 1L..DurationFormatter.MAX_MINUTES

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SlateDark800,
    shape = RoundedCornerShape(20.dp),
    icon = {
      Icon(
        imageVector = Icons.Default.HourglassTop,
        contentDescription = "Duración",
        tint = AmberAccent,
        modifier = Modifier.size(32.dp)
      )
    },
    title = {
      Text(
        text = "Duración Personalizada del Video",
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column {
        Text(
          text = "Ingresa la cantidad en minutos. El límite máximo soportado es de 1,000,000,000 minutos.",
          color = TextSecondary,
          fontSize = 13.sp,
          lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = textInput,
          onValueChange = { input ->
            val digits = input.filter { it.isDigit() }.take(10) // 10 digits handles up to 1 billion
            textInput = digits
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          label = { Text("Minutos") },
          suffix = { Text("min", color = TextSecondary) },
          isError = !isValid,
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SlateDark900,
            unfocusedContainerColor = SlateDark900,
            focusedBorderColor = CyanNeon,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("custom_duration_input_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick set max button
        OutlinedButton(
          onClick = { textInput = DurationFormatter.MAX_MINUTES.toString() },
          colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangeFlame),
          border = BorderStroke(1.dp, OrangeFlame.copy(alpha = 0.5f)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth().testTag("set_max_duration_button")
        ) {
          Icon(
            imageVector = Icons.Default.AllInclusive,
            contentDescription = "Máximo",
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Fijar Máximo: 1,000,000,000 min")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Conversion Preview Box
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = SlateDark900,
          border = BorderStroke(1.dp, SlateBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "Equivalencia de Tiempo:",
              color = TextSecondary,
              fontSize = 11.sp
            )
            Text(
              text = DurationFormatter.formatMinutesToHumanReadable(currentVal),
              color = CyanNeon,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (isValid) {
            onConfirm(currentVal)
          }
        },
        enabled = isValid,
        colors = ButtonDefaults.buttonColors(
          containerColor = CyanNeon,
          contentColor = SlateDark900
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("confirm_duration_button")
      ) {
        Text("Guardar Duración", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
        border = BorderStroke(1.dp, SlateBorder),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text("Cancelar")
      }
    }
  )
}
