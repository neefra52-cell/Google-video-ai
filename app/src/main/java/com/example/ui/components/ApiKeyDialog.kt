package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ApiKeyDialog(
  currentCustomKey: String,
  onDismiss: () -> Unit,
  onSaveKey: (String) -> Unit
) {
  var keyInput by remember { mutableStateOf(currentCustomKey) }
  val buildConfigKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }
  val isBuiltInKeyConfigured = buildConfigKey.isNotBlank() && buildConfigKey != "MY_GEMINI_API_KEY"

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SlateDark800,
    shape = RoundedCornerShape(20.dp),
    icon = {
      Icon(
        imageVector = Icons.Default.Security,
        contentDescription = "API Key",
        tint = CyanNeon,
        modifier = Modifier.size(32.dp)
      )
    },
    title = {
      Text(
        text = "Google Video AI & Veo API",
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column {
        Text(
          text = "Esta aplicación utiliza los modelos de Google Veo y Gemini AI para generar videos cinemáticos con audio de alta fidelidad.",
          color = TextSecondary,
          fontSize = 13.sp,
          lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SlateDark900,
          border = BorderStroke(1.dp, SlateBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (isBuiltInKeyConfigured || currentCustomKey.isNotBlank()) Icons.Default.CheckCircle else Icons.Default.Key,
              contentDescription = "Estado",
              tint = if (isBuiltInKeyConfigured || currentCustomKey.isNotBlank()) CyanNeon else AmberAccent,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = if (isBuiltInKeyConfigured || currentCustomKey.isNotBlank()) "Clave Gemini Conectada" else "Modo Simulación Activo",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = if (isBuiltInKeyConfigured) "Inyectada de forma segura por AI Studio" else "Puedes ingresar tu clave o generar videos sin restricciones",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = keyInput,
          onValueChange = { keyInput = it },
          label = { Text("Clave Gemini API (Opcional)") },
          placeholder = { Text("AIzaSy...") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SlateDark900,
            unfocusedContainerColor = SlateDark900,
            focusedBorderColor = CyanNeon,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("custom_api_key_field")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSaveKey(keyInput.trim())
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = CyanNeon,
          contentColor = SlateDark900
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("save_api_key_button")
      ) {
        Text("Guardar", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
        border = BorderStroke(1.dp, SlateBorder),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text("Cerrar")
      }
    }
  )
}
