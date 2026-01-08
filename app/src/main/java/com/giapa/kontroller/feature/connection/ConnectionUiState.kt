package com.giapa.kontroller.feature.connection

import com.giapa.kontroller.util.IpAddressValidator

data class ConnectionUiState(
    val endpoint: String = "",
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
) {
    val isConnectEnabled: Boolean
        get() = IpAddressValidator.isValidEndpoint(endpoint) && !isConnecting
}
