package com.acaris.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val AcarisPrimary = Color(0xFF3674B5)
val AcarisSecondary = Color(0xFF578FCA)
val AcarisTertiary = Color(0xFFA1E3F9)
val AcarisLightAqua = Color(0xFFD1F8EF)

val AppBackgroundDark = Color(0xFF111111)
val AppBackgroundLight = Color(0xFFECF0F3)

val CardBackgroundDark = Color(0xFF222222)
val CardBackgroundLight = Color(0xFFF3F3F3)

val TextDark = Color(0xFF1A1A1A)
val TextLight = Color(0xFFFFFFFF)
val TextGray = Color(0xFF757575)
val TextCaption = Color(0xFF767779)
val SurfaceWhite = Color(0xFFFFFFFF)

val StatusAvailableBg: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF81C784).copy(alpha = 0.15f) else Color(0xFF4CAF50).copy(alpha = 0.2f)

val StatusAvailableText: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF81C784) else Color(0xFF4CAF50)

val StatusBookedBg: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF64B5F6).copy(alpha = 0.15f) else Color(0xFFE3F2FD)

val StatusBookedText: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF64B5F6) else Color(0xFF2196F3)

val StatusFullBg: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFFE57373).copy(alpha = 0.15f) else Color(0xFFF44336).copy(alpha = 0.2f)

val StatusFullText: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFFE57373) else Color(0xFFF44336)

val StatusSelesaiBg: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFFCA28).copy(alpha = 0.15f) else Color(0xFFFFF8E1)

val StatusSelesaiText: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFFCA28) else Color(0xFFFFA000)