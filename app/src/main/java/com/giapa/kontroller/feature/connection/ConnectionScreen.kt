package com.giapa.kontroller.feature.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.giapa.kontroller.ui.components.Rounded
import com.giapa.kontroller.ui.components.primaryButtonColors

@Composable
fun ConnectionRoute(
    onConnected: () -> Unit,
    viewModel: ConnectionViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ConnectionEvent.Connected -> onConnected()
            }
        }
    }

    ConnectionScreen(
        state = state,
        onEndpointChange = viewModel::onEndpointChange,
        onConnectClick = viewModel::onConnectClick,
        onScanClick = viewModel::onScanClick,
    )
}

@Composable
fun ConnectionScreen(
    state: ConnectionUiState,
    onEndpointChange: (String) -> Unit,
    onConnectClick: () -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Rounded.ScreenPadding),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "KOntroller",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = state.endpoint,
            onValueChange = onEndpointChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = Rounded.Field,
            label = { Text("Address") },
            supportingText = {
                val msg = state.errorMessage
                if (msg != null) Text(msg, color = MaterialTheme.colorScheme.error)
            },
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onConnectClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isConnectEnabled,
            shape = Rounded.Button,
            colors = primaryButtonColors(),
        ) {
            Text(if (state.isConnecting) "Connecting…" else "Connect")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onScanClick,
            modifier = Modifier.fillMaxWidth(),
            shape = Rounded.Button,
        ) {
            Text("Scan")
        }
    }
}

