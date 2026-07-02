package com.acaris.core.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 🌟 CUSTOM MODIFIER: glowShadow
 * Digunakan untuk membuat bayangan berwarna (neon/glow) dengan ketebalan (alpha)
 * yang bisa diatur secara bebas. Sangat cocok untuk mendobrak batasan shadow bawaan Android.
 */
fun Modifier.glowShadow(
    color: Color,
    alpha: Float = 0.4f,
    borderRadius: Dp = 0.dp,
    blurRadius: Dp = 8.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp
) = this.drawBehind {
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparentColor = color.copy(alpha = 0f).toArgb()

    // 🌟 1. BUAT CETAKAN (PATH) SEUKURAN KOMPONEN ASLINYA
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                cornerRadius = CornerRadius(borderRadius.toPx())
            )
        )
    }

    // 🌟 2. GUNAKAN "ClipOp.Difference" UNTUK MELUBANGI BAGIAN TENGAH
    // Ini artinya: "Tolong gambar bayangan HANYA di area yang BUKAN bagian dari Path/Cetakan di atas"
    clipPath(path, clipOp = ClipOp.Difference) {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.color = transparentColor
            frameworkPaint.setShadowLayer(
                blurRadius.toPx(),
                offsetX.toPx(),
                offsetY.toPx(),
                shadowColor
            )
            canvas.drawRoundRect(
                0f,
                0f,
                this.size.width,
                this.size.height,
                borderRadius.toPx(),
                borderRadius.toPx(),
                paint
            )
        }
    }
}