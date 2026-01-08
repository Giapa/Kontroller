package com.giapa.kontroller.feature.controls

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giapa.kontroller.domain.ControllerClient
import com.giapa.kontroller.domain.HttpControllerClient
import com.giapa.kontroller.voice.PlatformVoiceCommandListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ControlsViewModel(
    private val client: ControllerClient = HttpControllerClient(),
) : ViewModel() {

    private val _state = MutableStateFlow(ControlsUiState())
    val state: StateFlow<ControlsUiState> = _state

    private var voice: PlatformVoiceCommandListener? = null

    fun onBack() = send("Back") { client.sendBack() }
    fun onForward() = send("Forward") { client.sendForward() }

    fun onMicToggle(appContext: Context) {
        val currentlyListening = _state.value.isListening

        if (currentlyListening) {
            voice?.stop()
            _state.update { it.copy(isListening = false) }
            return
        }

        _state.update { it.copy(errorMessage = null) }

        val listener = voice ?: PlatformVoiceCommandListener(appContext.applicationContext)
            .also { voice = it }

        _state.update { it.copy(isListening = true) }

        listener.start(
            onText = ::onVoiceText,
            onError = { msg ->
                _state.update { it.copy(isListening = false, errorMessage = msg) }
            },
        )

        _state.update { it.copy(isListening = listener.isRunning()) }
    }

    override fun onCleared() {
        voice?.stop()
        super.onCleared()
    }

    private fun onVoiceText(text: String) {
        val t = text.trim().lowercase()
        when {
            t.contains("next") || t.contains("forward") -> onForward()
            t.contains("back") || t.contains("previous") || t.contains("prev") -> onBack()
        }
    }

    private fun send(label: String, action: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            action()
                .onSuccess { _state.update { it.copy(lastAction = label, errorMessage = null) } }
                .onFailure { e -> _state.update { it.copy(errorMessage = e.message ?: "Action failed") } }
        }
    }
}
