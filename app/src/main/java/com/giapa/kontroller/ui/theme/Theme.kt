package com.giapa.kontroller.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = NearBlack,
    primaryContainer = BrandPurpleDark,
    onPrimaryContainer = White,

    secondary = PurpleGrey80,
    onSecondary = NearBlack,

    background = NearBlack,
    onBackground = White,
    surface = Color(0xFF121114),
    onSurface = White,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = White,
    primaryContainer = BrandPurple,
    onPrimaryContainer = White,

    secondary = PurpleGrey40,
    onSecondary = White,

    background = AlmostWhite,
    onBackground = NearBlack,
    surface = White,
    onSurface = NearBlack,
)

@Composable
fun KOntrollerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}