// app/src/main/java/com/example/api_meteo/viewmodel/YtbDownloadViewModel.kt
package com.example.api_meteo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api_meteo.repository.YtbDownloadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class YtbDownloadState {
    object Idle : YtbDownloadState()
    object Loading : YtbDownloadState()
    data class Success(val file: File) : YtbDownloadState()
    data class Error(val message: String) : YtbDownloadState()
}

class YtbDownloadViewModel(
    private val repository: YtbDownloadRepository
) : ViewModel() {

    private val _downloadState = MutableStateFlow<YtbDownloadState>(YtbDownloadState.Idle)
    val downloadState: StateFlow<YtbDownloadState> = _downloadState

    fun downloadVideo(ytbUrl: String, downloadDir: String) {
        viewModelScope.launch {
            _downloadState.value = YtbDownloadState.Loading
            try {
                val result = repository.downloadYouTubeVideo(ytbUrl, downloadDir)
                _downloadState.value = result.fold(
                    onSuccess = { file -> YtbDownloadState.Success(file) },
                    onFailure = { throwable -> YtbDownloadState.Error(throwable.message ?: "Erreur inconnue") }
                )
            } catch (e: Exception) {
                Log.e("YtbDownloadViewModel", "Exception during download", e)
                _downloadState.value = YtbDownloadState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }


    fun resetState() {
        _downloadState.value = YtbDownloadState.Idle
    }
}
