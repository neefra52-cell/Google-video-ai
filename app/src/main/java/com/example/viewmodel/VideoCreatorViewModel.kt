package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioSynthesizer
import com.example.model.AspectRatio
import com.example.model.AudioTheme
import com.example.model.CameraMotion
import com.example.model.GeneratedVideo
import com.example.model.SelectedImage
import com.example.service.GeminiVeoService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

data class UiState(
  val selectedImage: SelectedImage = SelectedImage.presets.first(),
  val customImageUri: Uri? = null,
  val promptText: String = "Google Video AI cinemático hiperrealista con sonido envolvente en 8K, cámara dinámica y música ambiental futurista",
  val durationMinutes: Long = 1_000_000_000L,
  val aspectRatio: AspectRatio = AspectRatio.RATIO_16_9,
  val cameraMotion: CameraMotion = CameraMotion.ZOOM_IN,
  val audioTheme: AudioTheme = AudioTheme.SCI_FI_SYNTH,
  val soundEnabled: Boolean = true,
  val volume: Float = 0.8f,
  val narratorEnabled: Boolean = true,
  val narratorVoice: String = "Kore",
  val isGenerating: Boolean = false,
  val generationStep: Int = 0,
  val generationProgress: Float = 0f,
  val generationStatusText: String = "",
  val currentVideo: GeneratedVideo? = null,
  val savedVideos: List<GeneratedVideo> = emptyList(),
  val isPlaying: Boolean = false,
  val playbackPositionSeconds: Long = 0L,
  val isMuted: Boolean = false,
  val isFullscreen: Boolean = false,
  val activeTab: Int = 0, // 0 = Estudio, 1 = Reproductor, 2 = Biblioteca, 3 = Info / API
  val isEnhancingPrompt: Boolean = false,
  val showDurationDialog: Boolean = false,
  val showApiKeyDialog: Boolean = false,
  val customApiKey: String = ""
)

class VideoCreatorViewModel(application: Application) : AndroidViewModel(application) {
  private val veoService = GeminiVeoService(application)
  val audioSynthesizer = AudioSynthesizer(application)

  private val _uiState = MutableStateFlow(UiState())
  val uiState: StateFlow<UiState> = _uiState.asStateFlow()

  val visualizerAmplitudes: StateFlow<FloatArray> = audioSynthesizer.visualizerAmplitudes

  private var playbackJob: Job? = null

  init {
    // Initial generated video ready to play immediately with sound and 1,000,000,000 min duration
    val starter = GeneratedVideo(
      id = UUID.randomUUID().toString(),
      title = "Google Video AI (1,000,000,000 min)",
      prompt = "Google Video AI cinemático hiperrealista con sonido envolvente en 8K, cámara dinámica y música ambiental futurista",
      enhancedPrompt = "Google Veo 8K Video: Secuencia cinemática continua con sonido espacial 3D, dinámica de cámara orbital, partículas atmosféricas y audio sintetizado en tiempo real. Duración máxima: 1,000,000,000 minutos (~1,902.6 años).",
      imageUriString = null,
      imageResId = SelectedImage.presets.first().drawableResId,
      durationMinutes = 1_000_000_000L,
      cameraMotion = CameraMotion.ZOOM_IN,
      audioTheme = AudioTheme.SCI_FI_SYNTH,
      soundEnabled = true,
      narratorScript = "Google Video AI: Generando secuencia cinemática ultra-larga con sonido envolvente y movimiento continuo hasta mil millones de minutos.",
      createdAt = System.currentTimeMillis()
    )
    _uiState.value = _uiState.value.copy(
      currentVideo = starter,
      savedVideos = listOf(starter),
      isPlaying = true
    )
    // Start sound playback right away!
    startPlayback()
  }

  fun selectPresetImage(preset: SelectedImage) {
    _uiState.value = _uiState.value.copy(
      selectedImage = preset,
      customImageUri = null
    )
  }

  fun setCustomImage(uri: Uri) {
    _uiState.value = _uiState.value.copy(
      customImageUri = uri,
      selectedImage = SelectedImage(
        id = "custom-${System.currentTimeMillis()}",
        uriString = uri.toString(),
        title = "Imagen seleccionada",
        aspectRatio = _uiState.value.aspectRatio
      )
    )
  }

  fun updatePrompt(text: String) {
    _uiState.value = _uiState.value.copy(promptText = text)
  }

  fun updateDuration(minutes: Long) {
    val clamped = minutes.coerceIn(1L, 1_000_000_000L)
    _uiState.value = _uiState.value.copy(durationMinutes = clamped)
  }

  fun updateAspectRatio(ratio: AspectRatio) {
    _uiState.value = _uiState.value.copy(aspectRatio = ratio)
  }

  fun updateCameraMotion(motion: CameraMotion) {
    _uiState.value = _uiState.value.copy(cameraMotion = motion)
  }

  fun updateAudioTheme(theme: AudioTheme) {
    _uiState.value = _uiState.value.copy(audioTheme = theme)
    if (_uiState.value.isPlaying && _uiState.value.soundEnabled) {
      audioSynthesizer.playSound(
        scope = viewModelScope,
        theme = theme,
        narratorScript = _uiState.value.currentVideo?.narratorScript,
        enableNarrator = false
      )
    }
  }

  fun toggleSound(enabled: Boolean) {
    _uiState.value = _uiState.value.copy(soundEnabled = enabled)
    if (!enabled) {
      audioSynthesizer.stopSound()
    } else if (_uiState.value.isPlaying) {
      audioSynthesizer.playSound(
        scope = viewModelScope,
        theme = _uiState.value.audioTheme,
        narratorScript = _uiState.value.currentVideo?.narratorScript,
        enableNarrator = _uiState.value.narratorEnabled
      )
    }
  }

  fun updateVolume(volume: Float) {
    val clamped = volume.coerceIn(0f, 1f)
    _uiState.value = _uiState.value.copy(volume = clamped)
    audioSynthesizer.setVolume(if (_uiState.value.isMuted) 0f else clamped)
  }

  fun toggleMute() {
    val newMute = !_uiState.value.isMuted
    _uiState.value = _uiState.value.copy(isMuted = newMute)
    audioSynthesizer.setVolume(if (newMute) 0f else _uiState.value.volume)
  }

  fun toggleNarrator(enabled: Boolean) {
    _uiState.value = _uiState.value.copy(narratorEnabled = enabled)
  }

  fun setTab(index: Int) {
    _uiState.value = _uiState.value.copy(activeTab = index)
  }

  fun toggleFullscreen() {
    _uiState.value = _uiState.value.copy(isFullscreen = !_uiState.value.isFullscreen)
  }

  fun setDurationDialogVisible(visible: Boolean) {
    _uiState.value = _uiState.value.copy(showDurationDialog = visible)
  }

  fun setApiKeyDialogVisible(visible: Boolean) {
    _uiState.value = _uiState.value.copy(showApiKeyDialog = visible)
  }

  fun updateCustomApiKey(key: String) {
    _uiState.value = _uiState.value.copy(customApiKey = key)
  }

  fun enhancePromptWithAI() {
    val state = _uiState.value
    if (state.promptText.isBlank()) return

    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isEnhancingPrompt = true)
      val (enhanced, _) = veoService.enhancePrompt(
        userPrompt = state.promptText,
        durationMinutes = state.durationMinutes,
        cameraMotion = state.cameraMotion,
        audioTheme = state.audioTheme,
        customKey = state.customApiKey
      )
      _uiState.value = _uiState.value.copy(
        promptText = enhanced,
        isEnhancingPrompt = false
      )
    }
  }

  fun startGeneration() {
    val state = _uiState.value
    if (state.isGenerating) return

    stopPlayback()

    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(
        isGenerating = true,
        generationStep = 1,
        generationProgress = 0.1f,
        generationStatusText = "Paso 1/4: Analizando imagen y encuadre óptico..."
      )

      delay(900)
      _uiState.value = _uiState.value.copy(
        generationStep = 2,
        generationProgress = 0.35f,
        generationStatusText = "Paso 2/4: Generando vectores de movimiento de cámara (${state.cameraMotion.label})..."
      )

      delay(1100)
      _uiState.value = _uiState.value.copy(
        generationStep = 3,
        generationProgress = 0.65f,
        generationStatusText = "Paso 3/4: Sintetizando pista de sonido envolvente (${state.audioTheme.label})..."
      )

      val result = veoService.generateVeoVideo(
        prompt = state.promptText,
        imageUri = state.customImageUri,
        durationMinutes = state.durationMinutes,
        cameraMotion = state.cameraMotion,
        audioTheme = state.audioTheme,
        customKey = state.customApiKey
      )

      delay(900)
      _uiState.value = _uiState.value.copy(
        generationStep = 4,
        generationProgress = 0.95f,
        generationStatusText = "Paso 4/4: Renderizando video de Google Veo a 60fps con audio..."
      )
      delay(800)

      val newVideo = GeneratedVideo(
        id = UUID.randomUUID().toString(),
        title = state.promptText.take(28).trim().ifBlank { "Video AI Generado" },
        prompt = state.promptText,
        enhancedPrompt = result.enhancedPrompt,
        imageUriString = state.customImageUri?.toString(),
        imageResId = state.selectedImage.drawableResId,
        durationMinutes = state.durationMinutes,
        cameraMotion = state.cameraMotion,
        audioTheme = state.audioTheme,
        soundEnabled = state.soundEnabled,
        narratorScript = result.narrationScript,
        createdAt = System.currentTimeMillis()
      )

      val updatedList = listOf(newVideo) + state.savedVideos

      _uiState.value = _uiState.value.copy(
        isGenerating = false,
        generationStep = 0,
        generationProgress = 1.0f,
        generationStatusText = "",
        currentVideo = newVideo,
        savedVideos = updatedList,
        activeTab = 1, // Switch to Player tab
        playbackPositionSeconds = 0L
      )

      // Automatically play the newly generated video with sound!
      startPlayback()
    }
  }

  fun togglePlayPause() {
    if (_uiState.value.isPlaying) {
      pausePlayback()
    } else {
      startPlayback()
    }
  }

  private fun startPlayback() {
    val video = _uiState.value.currentVideo ?: return
    _uiState.value = _uiState.value.copy(isPlaying = true)

    if (_uiState.value.soundEnabled && !_uiState.value.isMuted) {
      audioSynthesizer.playSound(
        scope = viewModelScope,
        theme = video.audioTheme,
        narratorScript = video.narratorScript,
        enableNarrator = _uiState.value.narratorEnabled
      )
    }

    playbackJob?.cancel()
    playbackJob = viewModelScope.launch {
      val totalSec = video.durationMinutes * 60L
      while (isActive && _uiState.value.isPlaying) {
        delay(1000)
        val nextPos = _uiState.value.playbackPositionSeconds + 1
        if (nextPos >= totalSec && totalSec > 0) {
          // Loop seamlessly
          _uiState.value = _uiState.value.copy(playbackPositionSeconds = 0L)
        } else {
          _uiState.value = _uiState.value.copy(playbackPositionSeconds = nextPos)
        }
      }
    }
  }

  private fun pausePlayback() {
    _uiState.value = _uiState.value.copy(isPlaying = false)
    playbackJob?.cancel()
    audioSynthesizer.pauseSound()
  }

  fun stopPlayback() {
    _uiState.value = _uiState.value.copy(isPlaying = false, playbackPositionSeconds = 0L)
    playbackJob?.cancel()
    audioSynthesizer.stopSound()
  }

  fun seekTo(seconds: Long) {
    val total = (_uiState.value.currentVideo?.durationMinutes ?: 1L) * 60L
    val clamped = seconds.coerceIn(0L, total)
    _uiState.value = _uiState.value.copy(playbackPositionSeconds = clamped)
  }

  fun playSavedVideo(video: GeneratedVideo) {
    stopPlayback()
    _uiState.value = _uiState.value.copy(
      currentVideo = video,
      activeTab = 1,
      playbackPositionSeconds = 0L,
      audioTheme = video.audioTheme
    )
    startPlayback()
  }

  fun deleteVideo(id: String) {
    val updated = _uiState.value.savedVideos.filter { it.id != id }
    val newCurrent = if (_uiState.value.currentVideo?.id == id) {
      updated.firstOrNull()
    } else {
      _uiState.value.currentVideo
    }
    _uiState.value = _uiState.value.copy(
      savedVideos = updated,
      currentVideo = newCurrent
    )
  }

  override fun onCleared() {
    super.onCleared()
    audioSynthesizer.release()
  }
}
