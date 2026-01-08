package com.giapa.kontroller.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

object Rounded {
    val ScreenPadding = 20.dp
    val Card = RoundedCornerShape(20.dp)
    val Button = RoundedCornerShape(16.dp)
    val Field = RoundedCornerShape(16.dp)
}

@Composable
fun primaryButtonColors() = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary,
)

