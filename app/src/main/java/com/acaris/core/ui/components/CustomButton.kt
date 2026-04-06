package com.acaris.core.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true // 🌟 PARAMETER BARU DITAMBAHKAN
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animasi hanya berjalan jika tombol aktif
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "PrimaryButtonScaleAnimation"
    )

    Button(
        onClick = onClick,
        modifier = modifier.scale(scale).height(60.dp),
        shape = RoundedCornerShape(30.dp),
        interactionSource = interactionSource,
        enabled = enabled, // 🌟 DISAMBUNGKAN KE KOMPONEN BAWAAN
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.background,
            // 🌟 Warna saat tombol mati (Disabled)
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp
        )
    }
}

@Composable
fun CustomOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true // 🌟 PARAMETER BARU DITAMBAHKAN
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animasi hanya berjalan jika tombol aktif
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "OutlinedButtonScaleAnimation"
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.scale(scale).height(60.dp),
        shape = RoundedCornerShape(30.dp),
        interactionSource = interactionSource,
        enabled = enabled, // 🌟 DISAMBUNGKAN KE KOMPONEN BAWAAN
        // 🌟 Border memudar saat tombol mati
        border = BorderStroke(2.dp, if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.background,
            // 🌟 Teks memudar saat tombol mati
            disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp
        )
    }
}