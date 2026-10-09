package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AspectRatio
import com.example.model.CameraMotion
import com.example.model.SelectedImage
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
fun ImagePickerSection(
  selectedImage: SelectedImage,
  customImageUri: Uri?,
  aspectRatio: AspectRatio,
  cameraMotion: CameraMotion,
  onPresetSelected: (SelectedImage) -> Unit,
  onCustomImagePicked: (Uri) -> Unit,
  onAspectRatioSelected: (AspectRatio) -> Unit,
  onCameraMotionSelected: (CameraMotion) -> Unit,
  modifier: Modifier = Modifier
) {
  // Photo Picker launcher compliant with Play Policy
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    uri?.let { onCustomImagePicked(it) }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("image_picker_section"),
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
            imageVector = Icons.Default.PhotoLibrary,
            contentDescription = "Imagen de origen",
            tint = CyanNeon,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "1. Elegir Imagen Base",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        }

        OutlinedButton(
          onClick = {
            photoPickerLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
          colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanNeon),
          border = BorderStroke(1.dp, CyanNeon),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("pick_gallery_image_button")
        ) {
          Icon(
            imageVector = Icons.Default.AddPhotoAlternate,
            contentDescription = "Subir foto",
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Galería", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Presets & Custom Image Preview Row
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        // If custom image is selected, show it as first item
        if (customImageUri != null) {
          item {
            Box(
              modifier = Modifier
                .width(130.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, CyanNeon, RoundedCornerShape(12.dp))
                .background(SlateDark900)
            ) {
              AsyncImage(
                model = customImageUri,
                contentDescription = "Imagen de galería",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
              Surface(
                color = CyanNeon,
                shape = RoundedCornerShape(bottomEnd = 8.dp),
                modifier = Modifier.align(Alignment.TopStart)
              ) {
                Text(
                  text = "Mi Foto",
                  color = SlateDark900,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }

        items(SelectedImage.presets) { preset ->
          val isSelected = (customImageUri == null && selectedImage.id == preset.id)
          Box(
            modifier = Modifier
              .width(130.dp)
              .height(90.dp)
              .clip(RoundedCornerShape(12.dp))
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) CyanNeon else SlateBorder,
                shape = RoundedCornerShape(12.dp)
              )
              .clickable { onPresetSelected(preset) }
          ) {
            if (preset.drawableResId != null) {
              Image(
                painter = painterResource(id = preset.drawableResId),
                contentDescription = preset.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            }
            if (isSelected) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Seleccionado",
                tint = CyanNeon,
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .padding(6.dp)
                  .size(18.dp)
              )
            }
            Surface(
              color = SlateDark900.copy(alpha = 0.8f),
              shape = RoundedCornerShape(topEnd = 8.dp),
              modifier = Modifier.align(Alignment.BottomStart)
            ) {
              Text(
                text = preset.title,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Aspect Ratio Selector
      Text(
        text = "Formato de Pantalla:",
        color = TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(6.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        AspectRatio.entries.forEach { ratio ->
          val selected = (aspectRatio == ratio)
          FilterChip(
            selected = selected,
            onClick = { onAspectRatioSelected(ratio) },
            label = { Text(ratio.label, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = CyanNeon.copy(alpha = 0.2f),
              selectedLabelColor = CyanNeon,
              containerColor = SlateDark700,
              labelColor = Color.White
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = selected,
              borderColor = SlateBorder,
              selectedBorderColor = CyanNeon
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Camera Motion Dynamics
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.CameraRoll,
          contentDescription = "Movimiento de cámara",
          tint = AmberAccent,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Dinámica de Movimiento de Cámara (Veo):",
          color = TextSecondary,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        CameraMotion.entries.forEach { motion ->
          val selected = (cameraMotion == motion)
          FilterChip(
            selected = selected,
            onClick = { onCameraMotionSelected(motion) },
            label = { Text(motion.label, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = AmberAccent.copy(alpha = 0.2f),
              selectedLabelColor = AmberAccent,
              containerColor = SlateDark700,
              labelColor = Color.White
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = selected,
              borderColor = SlateBorder,
              selectedBorderColor = AmberAccent
            )
          )
        }
      }
    }
  }
}
