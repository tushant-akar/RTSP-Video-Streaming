package com.tushant.rtspvideostreaming.viewModel

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.MediaSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VideoViewModel(
    val exoPlayer: ExoPlayer,
    private val appContext: Context
): ViewModel() {
    private val _streamUrl = MutableStateFlow("")
    val streamUrl: StateFlow<String> = _streamUrl

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun setStreamUrl(url: String) {
        _streamUrl.value = url
    }

    @OptIn(UnstableApi::class)
    fun startStreaming() {
        viewModelScope.launch {
            try {
                if (_streamUrl.value.isNotEmpty()) {
                    val mediaSource: MediaSource = RtspMediaSource.Factory()
                        .createMediaSource(MediaItem.fromUri(_streamUrl.value))
                    exoPlayer.setMediaSource(mediaSource)
                    exoPlayer.prepare()
                    exoPlayer.play()
                } else {
                    _errorMessage.value = "Please enter a valid RTSP URL."
                }
            } catch (e: IllegalArgumentException) {
                _errorMessage.value = "Invalid RTSP URL format."
            } catch (e: Exception) {
                _errorMessage.value = when {
                    e.message?.contains("timeout") == true -> "Connection timed out. Check your network."
                    e.message?.contains("codec") == true -> "Unsupported codec. Please use H.264 or AAC."
                    else -> "Error: ${e.message}"
                }
                Log.e("VideoViewModel", "Streaming error: ${e.message}")
            }
        }
    }

    fun pauseStreaming() {
        exoPlayer.pause()
    }

    fun stopStreaming() {
        exoPlayer.stop()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}