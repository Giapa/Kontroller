package com.giapa.kontroller.feature.controls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giapa.kontroller.domain.ControllerClient
import com.giapa.kontroller.domain.HttpControllerClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ControlsViewModel(
    private val client: ControllerClient = HttpControllerClient(),
) : ViewModel() {

    private val _state = MutableStateFlow(ControlsUiState())
    val state: StateFlow<ControlsUiState> = _state

    fun onBack() = send("Back") { client.sendBack() }
    fun onForward() = send("Forward") { client.sendForward() }
    fun onMic() = send("Mic") { client.micPress() }

    private fun send(label: String, action: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            val result = action()
            result
                .onSuccess { _state.update { it.copy(lastAction = label, errorMessage = null) } }
                .onFailure { e ->
                    _state.update {
                        it.copy(errorMessage = e.message ?: "Action failed")
                    }
                }
        }
    }
}
