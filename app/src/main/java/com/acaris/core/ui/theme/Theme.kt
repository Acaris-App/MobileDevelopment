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
    primary = AcarisPrimary,         // Biru Tua
    onPrimary = TextLight,           // Teks putih di atas tombol biru
    secondary = AcarisSecondary,     // Biru Sedang
    onSecondary = TextLight,         // Teks putih di atas elemen sekunder
    tertiary = AcarisTertiary,       // Biru Muda
    onTertiary = TextDark,           // Teks gelap di atas elemen biru muda
    background = AppBackgroundDark,  // Tetap Dark (Sesuai request)
    onBackground = AppBackgroundLight,
    surface = TextDark,              // Warna kartu/permukaan di mode dark
    onSurface = AppBackgroundLight
)

private val LightColorScheme = lightColorScheme(
    primary = AcarisPrimary,         // Biru Tua
    onPrimary = TextLight,           // Teks putih di atas tombol biru
    secondary = AcarisSecondary,     // Biru Sedang
    onSecondary = TextLight,         // Teks putih di atas elemen sekunder
    tertiary = AcarisTertiary,       // Biru Muda
    onTertiary = TextDark,           // Teks gelap di atas elemen biru muda
    background = AppBackgroundLight, // Tetap Light (Sesuai request)
    onBackground = AppBackgroundDark,
    surface = SurfaceWhite,          // 🌟 (Perbaikan) Permukaan kartu jadi putih agar bersih
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
        shapes = Shapes, // Pastikan variabel Shapes ini ada di file Shape.kt ya
        content = content
    )
}