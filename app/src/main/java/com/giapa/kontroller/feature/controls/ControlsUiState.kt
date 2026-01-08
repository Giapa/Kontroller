package com.giapa.kontroller.feature.controls

data class ControlsUiState(
    val lastAction: String? = null,
    val errorMessage: String? = null,
    val isListening: Boolean = false,
)
