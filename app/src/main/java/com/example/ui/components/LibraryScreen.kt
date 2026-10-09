package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.model.DurationFormatter
import com.example.model.GeneratedVideo
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LibraryScreen(
  videos: List<GeneratedVideo>,
  onPlayVideo: (GeneratedVideo) -> Unit,
  onDeleteVideo: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  if (videos.isEmpty()) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(32.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.VideoLibrary,
          contentDescription = "Sin videos",
          tint = TextSecondary,
          modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Aún no has generado videos",
          color = TextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Elige una imagen y escribe un texto para crear tu primer video con sonido.",
          color = TextSecondary,
          fontSize = 13.sp,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
      }
    }
    return
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    items(videos, key = { it.id }) { video ->
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDark800),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onPlayVideo(video) }
          .testTag("library_video_item_${video.id}")
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Thumbnail
          Box(
            modifier = Modifier
              .size(90.dp, 60.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateDark900)
          ) {
            if (!video.imageUriString.isNullOrBlank()) {
              AsyncImage(
                model = video.imageUriString,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            } else if (video.imageResId != null) {
              Image(
                painter = painterResource(id = video.imageResId),
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            }

            // Play overlay circle
            Surface(
              shape = CircleShape,
              color = SlateDark900.copy(alpha = 0.7f),
              modifier = Modifier
                .align(Alignment.Center)
                .size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Reproducir",
                tint = CyanNeon,
                modifier = Modifier
                  .padding(4.dp)
                  .size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          // Information
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = video.title,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = video.prompt,
              color = TextSecondary,
              fontSize = 12.sp,
              maxLines = 2
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyanNeon.copy(alpha = 0.15f)
              ) {
                Text(
                  text = DurationFormatter.formatMinutesToHumanReadable(video.durationMinutes),
                  color = CyanNeon,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }

              if (video.soundEnabled) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = AmberAccent.copy(alpha = 0.15f)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Audiotrack,
                      contentDescription = "Audio",
                      tint = AmberAccent,
                      modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                      text = video.audioTheme.label,
                      color = AmberAccent,
                      fontSize = 10.sp
                    )
                  }
                }
              }
            }
          }

          // Delete button
          IconButton(
            onClick = { onDeleteVideo(video.id) },
            modifier = Modifier.testTag("delete_video_button_${video.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Eliminar",
              tint = TextSecondary,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}
