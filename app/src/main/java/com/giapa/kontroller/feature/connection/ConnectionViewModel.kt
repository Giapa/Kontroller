package com.giapa.kontroller.feature.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giapa.kontroller.domain.ConnectionRepository
import com.giapa.kontroller.domain.DefaultConnectionRepository
import com.giapa.kontroller.util.IpAddressValidator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConnectionViewModel(
    private val repo: ConnectionRepository = DefaultConnectionRepository(),
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
            _state.update { it.copy(errorMessage = "Enter a valid IP address (e.g. 192.168.1.10)") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isConnecting = true, errorMessage = null) }
            val result = repo.connect(endpoint)
            _state.update { it.copy(isConnecting = false) }
            result
                .onSuccess {
                    _events.trySend(ConnectionEvent.ShowPopup("Success", "Connected to KOReader"))
                    _events.trySend(ConnectionEvent.Connected)
                }
                .onFailure { e ->
                    _events.trySend(
                        ConnectionEvent.ShowPopup(
                            "Connection error",
                            e.message ?: "Failed to connect",
                        ),
                    )
                }
        }
    }

    fun onScanClick() {
        viewModelScope.launch {
            _state.update { it.copy(isScanning = true, scanResults = emptyList(), errorMessage = null) }

            val result = repo.scanNetworkFor8080()

            _state.update { it.copy(isScanning = false) }

            result
                .onSuccess { ips ->
                    _state.update { it.copy(scanResults = ips) }
                    if (ips.isEmpty()) {
                        _events.trySend(
                            ConnectionEvent.ShowPopup(
                                "No devices found",
                                "No KOReader instances detected on port 8080.",
                            ),
                        )
                    }
                }
                .onFailure { e ->
                    _events.trySend(
                        ConnectionEvent.ShowPopup(
                            "Scan error",
                            e.message ?: "Failed to scan network",
                        ),
                    )
                }
        }
    }

    fun onScanResultClick(ip: String) {
        _state.update { it.copy(endpoint = ip, errorMessage = null) }
        // Reuse connect flow.
        onConnectClick()
    }
}

sealed interface ConnectionEvent {
    data object Connected : ConnectionEvent
    data class ShowPopup(val title: String, val message: String) : ConnectionEvent
}
