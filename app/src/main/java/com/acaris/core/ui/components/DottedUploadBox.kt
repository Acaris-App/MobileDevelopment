package com.acaris.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DottedUploadBox(
    text: String = "Tambah Dokumen",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stroke = Stroke(
        width = 4f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() }
            .drawBehind {
                drawRoundRect(color = Color.Gray, style = stroke)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("+", fontSize = 24.sp, color = Color.Gray)
            Text(text, color = Color.Gray)
        }
    }
}