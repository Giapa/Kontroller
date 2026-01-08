package com.giapa.kontroller.feature.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giapa.kontroller.domain.ConnectionRepository
import com.giapa.kontroller.domain.FakeConnectionRepository
import com.giapa.kontroller.util.IpAddressValidator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConnectionViewModel(
    private val repo: ConnectionRepository = FakeConnectionRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow(ConnectionUiState())
    val state: StateFlow<ConnectionUiState> = _state

    private val _events = Channel<ConnectionEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEndpointChange(value: String) {
        _state.update { it.copy(endpoint = value, errorMessage = null) }
    }

    fun onConnectClick() {
        val endpoint = state.value.endpoint.trim()
        if (!IpAddressValidator.isValidEndpoint(endpoint)) {
            _state.update { it.copy(errorMessage = "Enter a valid IP address (e.g. 192.168.1.10 or 192.168.1.10:8080)") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isConnecting = true, errorMessage = null) }
            val result = repo.connect(endpoint)
            _state.update { it.copy(isConnecting = false) }
            result
                .onSuccess { _events.trySend(ConnectionEvent.Connected) }
                .onFailure { e ->
                    _state.update { it.copy(errorMessage = e.message ?: "Failed to connect") }
                }
        }
    }

    fun onScanClick() {
        _state.update { it.copy(errorMessage = "Scan not implemented yet") }
    }
}

sealed interface ConnectionEvent {
    data object Connected : ConnectionEvent
}
