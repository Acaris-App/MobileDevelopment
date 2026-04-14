package com.acaris.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AcarisPrimary,         // Biru Tua
    onPrimary = CardBackgroundLight,           // Teks putih di atas tombol biru
    secondary = AcarisSecondary,     // Biru Sedang
    onSecondary = CardBackgroundLight,         // Teks putih di atas elemen sekunder
    tertiary = AcarisTertiary,       // Biru Muda
    onTertiary = AppBackgroundDark,           // Teks gelap di atas elemen biru muda
    background = AppBackgroundDark,
    onBackground = CardBackgroundLight, // Teks utama di atas background gelap layar
    surface = CardBackgroundDark,       // Background Card Dark (606060)
    onSurface = CardBackgroundLight     // Teks di atas Card Dark (f3f3f3)
)

private val LightColorScheme = lightColorScheme(
    primary = AcarisPrimary,         // Biru Tua
    onPrimary = CardBackgroundLight,           // Teks putih di atas tombol biru
    secondary = AcarisSecondary,     // Biru Sedang
    onSecondary = CardBackgroundLight,         // Teks putih di atas elemen sekunder
    tertiary = AcarisTertiary,       // Biru Muda
    onTertiary = AppBackgroundDark,           // Teks gelap di atas elemen biru muda
    background = AppBackgroundLight,
    onBackground = AppBackgroundDark,   // Teks utama di atas background terang layar
    surface = CardBackgroundLight,      // Background Card Light (f3f3f3)
    onSurface = AppBackgroundDark       // Teks di atas Card Light (0c0c0c)
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

    // 🌟 MENGUBAH WARNA BAR SISTEM ANDROID (STATUS BAR & NAV BAR)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // 1. Warna Bar Atas (Jam, Sinyal) mengikuti background layar
            window.statusBarColor = colorScheme.background.toArgb()

            // 2. Warna Bar Bawah (Tombol 3 Nav) mengikuti warna Bottom Nav
            window.navigationBarColor = if (darkTheme) {
                AcarisPrimary.toArgb()
            } else {
                AcarisTertiary.toArgb()
            }

            // 3. Menyesuaikan warna ikon (Hitam/Putih) agar kontras
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}