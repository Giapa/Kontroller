package com.giapa.kontroller.feature.controls

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.giapa.kontroller.ui.components.Rounded
import com.giapa.kontroller.ui.components.primaryButtonColors
import com.giapa.kontroller.util.KeepScreenOnWithTimeoutEffect

@Composable
fun ControlsRoute(
    onNavigateUp: () -> Unit,
    viewModel: ControlsViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    KeepScreenOnWithTimeoutEffect(
        timeoutMs = ControlsViewModel.KEEP_SCREEN_ON_MS,
        bumpKey = state.keepScreenOnBump,
    )

    DisposableEffect(Unit) {
        viewModel.onControlsShown(context)
        onDispose {
            viewModel.onControlsHidden()
        }
    }

    ControlsScreen(
        state = state,
        onNavigateUp = onNavigateUp,
        onBack = viewModel::onBack,
        onForward = viewModel::onForward,
        onMic = { viewModel.onMicToggle(context) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlsScreen(
    state: ControlsUiState,
    onNavigateUp: () -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onMic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigateUp = onNavigateUp

    val context = LocalContext.current
    var micPermissionError by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                micPermissionError = null
                onMic()
            } else {
                micPermissionError = "Microphone permission is required for voice control"
            }
        },
    )

    fun toggleMicWithPermission() {
        if (state.isListening) {
            onMic()
            return
        }

        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO,
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            micPermissionError = null
            onMic()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Controls") },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(Rounded.ScreenPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    shape = Rounded.Button,
                    colors = primaryButtonColors(),
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
                    Text("Back")
                }

                Button(
                    onClick = onForward,
                    modifier = Modifier.weight(1f),
                    shape = Rounded.Button,
                    colors = primaryButtonColors(),
                ) {
                    Text("Forward")
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                }
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = { toggleMicWithPermission() },
                modifier = Modifier.fillMaxWidth(),
                shape = Rounded.Button,
                colors = primaryButtonColors(),
            ) {
                Icon(Icons.Default.Mic, contentDescription = null)
                Text(if (state.isListening) "Stop" else "Mic")
            }

            val err = state.errorMessage ?: micPermissionError
            if (err != null) {
                Spacer(Modifier.height(12.dp))
                Text(err, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
