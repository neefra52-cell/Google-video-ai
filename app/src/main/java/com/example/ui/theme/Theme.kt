package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CinematicColorScheme = darkColorScheme(
  primary = CyanNeon,
  onPrimary = SlateDark900,
  primaryContainer = CyanNeonDark,
  onPrimaryContainer = Color.White,
  secondary = AmberAccent,
  onSecondary = SlateDark900,
  secondaryContainer = OrangeFlame,
  onSecondaryContainer = Color.White,
  tertiary = PurpleNeon,
  onTertiary = Color.White,
  background = SlateDark900,
  onBackground = TextPrimary,
  surface = SlateDark800,
  onSurface = TextPrimary,
  surfaceVariant = SlateDark700,
  onSurfaceVariant = TextSecondary,
  outline = SlateBorder,
  outlineVariant = SlateDark600,
  error = CrimsonRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      window?.let {
        it.statusBarColor = SlateDark900.toArgb()
        it.navigationBarColor = SlateDark900.toArgb()
        WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = CinematicColorScheme,
    typography = Typography,
    content = content
  )
}
