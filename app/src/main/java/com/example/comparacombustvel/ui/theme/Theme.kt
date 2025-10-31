package com.example.comparacombustvel.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// PALETA ESCURA (Usando as cores de Color.kt)
private val DarkColorScheme = darkColorScheme(
    primary = BlueLight,
    onPrimary = Black,
    secondary = Blue,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    outline = BlueLight,
    error = Red
)

// PALETA CLARA (Usando as cores de Color.kt)
private val LightColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = WhitePure,
    secondary = BlueLight,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    outline = Blue,
    error = Red
)

@Composable
fun ComparaCombustivelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
        content = content
    )
}