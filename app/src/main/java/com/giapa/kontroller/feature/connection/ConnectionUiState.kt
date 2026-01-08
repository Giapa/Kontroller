package com.giapa.kontroller.feature.connection

data class ConnectionUiState(
    val endpoint: String = "",
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
) {
    val isConnectEnabled: Boolean get() = endpoint.isNotBlank() && !isConnecting
}

