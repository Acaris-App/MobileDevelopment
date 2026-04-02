package com.acaris.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PastelBlue,
    onPrimary = TextDark,
    secondary = PastelMint,
    onSecondary = TextDark,
    background = TextDark,
    onBackground = SurfaceWhite,
    surface = TextDark,
    onSurface = SurfaceWhite
)

private val LightColorScheme = lightColorScheme(
    primary = PastelBlue,
    onPrimary = TextDark,
    secondary = PastelMint,
    onSecondary = TextDark,
    background = SurfaceWhite,
    onBackground = TextDark,
    surface = PastelYellow,
    onSurface = TextDark
)

@Composable
fun AcarisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        shapes = Shapes,
        content = content
    )
}