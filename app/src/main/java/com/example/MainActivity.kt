package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ApiKeyDialog
import com.example.ui.components.DurationInputDialog
import com.example.ui.components.DurationSelectorSection
import com.example.ui.components.GeneratingDialog
import com.example.ui.components.ImagePickerSection
import com.example.ui.components.LibraryScreen
import com.example.ui.components.PromptSection
import com.example.ui.components.SoundConfigSection
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.VideoCreatorViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainScreen()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: VideoCreatorViewModel = viewModel()) {
  val state by viewModel.uiState.collectAsState()
  val amplitudes by viewModel.visualizerAmplitudes.collectAsState()

  // Handle Android back button
  BackHandler(enabled = state.activeTab != 0) {
    viewModel.setTab(0)
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.MovieCreation,
                contentDescription = "Logo",
                tint = CyanNeon,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Google Video AI",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
              )
            }
            Text(
              text = "Con Sonido • Duración máx: 1,000,000,000 min",
              color = AmberAccent,
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium
            )
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.setApiKeyDialogVisible(true) },
            modifier = Modifier.testTag("top_bar_settings_button")
          ) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = "Configuración y API",
              tint = TextSecondary
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = SlateDark900
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = SlateDark900,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        NavigationBarItem(
          selected = state.activeTab == 0,
          onClick = { viewModel.setTab(0) },
          icon = {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Crear Video")
          },
          label = { Text("Crear", fontSize = 12.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = SlateDark900,
            selectedTextColor = CyanNeon,
            indicatorColor = CyanNeon,
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextSecondary
          ),
          modifier = Modifier.testTag("nav_tab_create")
        )

        NavigationBarItem(
          selected = state.activeTab == 1,
          onClick = { viewModel.setTab(1) },
          icon = {
            Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "Reproductor")
          },
          label = { Text("Reproductor", fontSize = 12.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = SlateDark900,
            selectedTextColor = CyanNeon,
            indicatorColor = CyanNeon,
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextSecondary
          ),
          modifier = Modifier.testTag("nav_tab_player")
        )

        NavigationBarItem(
          selected = state.activeTab == 2,
          onClick = { viewModel.setTab(2) },
          icon = {
            BadgedBox(
              badge = {
                if (state.savedVideos.isNotEmpty()) {
                  Badge(containerColor = AmberAccent, contentColor = SlateDark900) {
                    Text(text = state.savedVideos.size.toString())
                  }
                }
              }
            ) {
              Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = "Biblioteca")
            }
          },
          label = { Text("Biblioteca", fontSize = 12.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = SlateDark900,
            selectedTextColor = CyanNeon,
            indicatorColor = CyanNeon,
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextSecondary
          ),
          modifier = Modifier.testTag("nav_tab_library")
        )
      }
    },
    containerColor = SlateDark900
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (state.activeTab) {
        0 -> {
          // Video Creation Studio Tab
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            item {
              androidx.compose.material3.Button(
                onClick = { viewModel.startGeneration() },
                enabled = !state.isGenerating,
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                  containerColor = CyanNeon,
                  contentColor = SlateDark900
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(52.dp)
                  .testTag("quick_generate_top_button")
              ) {
                Icon(
                  imageVector = Icons.Default.MovieCreation,
                  contentDescription = "Generar",
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "⚡ GENERAR VIDEO AHORA CON SONIDO",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 14.sp
                )
              }
            }

            // Current Video Preview player card (if available)
            if (state.currentVideo != null) {
              item {
                Text(
                  text = "Video Activo:",
                  color = TextSecondary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                VideoPlayerView(
                  video = state.currentVideo,
                  isPlaying = state.isPlaying,
                  playbackPositionSeconds = state.playbackPositionSeconds,
                  isMuted = state.isMuted,
                  isFullscreen = state.isFullscreen,
                  volume = state.volume,
                  visualizerAmplitudes = amplitudes,
                  onPlayPauseToggle = { viewModel.togglePlayPause() },
                  onSeek = { viewModel.seekTo(it) },
                  onToggleMute = { viewModel.toggleMute() },
                  onToggleFullscreen = { viewModel.toggleFullscreen() }
                )
              }
            }

            // 1. Image Picker
            item {
              ImagePickerSection(
                selectedImage = state.selectedImage,
                customImageUri = state.customImageUri,
                aspectRatio = state.aspectRatio,
                cameraMotion = state.cameraMotion,
                onPresetSelected = { viewModel.selectPresetImage(it) },
                onCustomImagePicked = { viewModel.setCustomImage(it) },
                onAspectRatioSelected = { viewModel.updateAspectRatio(it) },
                onCameraMotionSelected = { viewModel.updateCameraMotion(it) }
              )
            }

            // 2. Prompt Input & AI Enhancement
            item {
              PromptSection(
                promptText = state.promptText,
                isEnhancing = state.isEnhancingPrompt,
                isGenerating = state.isGenerating,
                onPromptChanged = { viewModel.updatePrompt(it) },
                onEnhanceWithAI = { viewModel.enhancePromptWithAI() },
                onGenerateClick = { viewModel.startGeneration() }
              )
            }

            // 3. Duration Selector (up to 1,000,000,000 minutes!)
            item {
              DurationSelectorSection(
                currentMinutes = state.durationMinutes,
                onDurationSelected = { viewModel.updateDuration(it) },
                onOpenCustomDurationDialog = { viewModel.setDurationDialogVisible(true) }
              )
            }

            // 4. Sound & Audio Config
            item {
              SoundConfigSection(
                soundEnabled = state.soundEnabled,
                audioTheme = state.audioTheme,
                volume = state.volume,
                narratorEnabled = state.narratorEnabled,
                visualizerAmplitudes = amplitudes,
                onSoundToggle = { viewModel.toggleSound(it) },
                onThemeSelected = { viewModel.updateAudioTheme(it) },
                onVolumeChanged = { viewModel.updateVolume(it) },
                onNarratorToggle = { viewModel.toggleNarrator(it) }
              )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
          }
        }

        1 -> {
          // Dedicated Full Cinematic Player Tab
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
            verticalArrangement = Arrangement.Center
          ) {
            VideoPlayerView(
              video = state.currentVideo,
              isPlaying = state.isPlaying,
              playbackPositionSeconds = state.playbackPositionSeconds,
              isMuted = state.isMuted,
              isFullscreen = state.isFullscreen,
              volume = state.volume,
              visualizerAmplitudes = amplitudes,
              onPlayPauseToggle = { viewModel.togglePlayPause() },
              onSeek = { viewModel.seekTo(it) },
              onToggleMute = { viewModel.toggleMute() },
              onToggleFullscreen = { viewModel.toggleFullscreen() }
            )

            if (state.currentVideo != null) {
              Spacer(modifier = Modifier.height(16.dp))
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = SlateDark800,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text(
                    text = "Detalles del Video AI:",
                    color = AmberAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = state.currentVideo?.enhancedPrompt ?: state.currentVideo?.prompt ?: "",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                  )
                }
              }
            }
          }
        }

        2 -> {
          // Saved Creations Library Tab
          LibraryScreen(
            videos = state.savedVideos,
            onPlayVideo = { viewModel.playSavedVideo(it) },
            onDeleteVideo = { viewModel.deleteVideo(it) }
          )
        }
      }

      // Generation Progress Dialog
      if (state.isGenerating) {
        GeneratingDialog(
          step = state.generationStep,
          progress = state.generationProgress,
          statusText = state.generationStatusText
        )
      }

      // Custom Duration Input Dialog
      if (state.showDurationDialog) {
        DurationInputDialog(
          initialMinutes = state.durationMinutes,
          onDismiss = { viewModel.setDurationDialogVisible(false) },
          onConfirm = { minutes ->
            viewModel.updateDuration(minutes)
            viewModel.setDurationDialogVisible(false)
          }
        )
      }

      // API Key / Info Dialog
      if (state.showApiKeyDialog) {
        ApiKeyDialog(
          currentCustomKey = state.customApiKey,
          onDismiss = { viewModel.setApiKeyDialogVisible(false) },
          onSaveKey = { key ->
            viewModel.updateCustomApiKey(key)
          }
        )
      }
    }
  }
}
