package com.acaris.core.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background // 🌟 Tambahan import background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues // 🌟 Tambahan import PaddingValues
import androidx.compose.foundation.layout.fillMaxSize // 🌟 Tambahan import fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.theme.DisabledPrimaryGradient // 🌟 Import Gradient Global
import com.acaris.core.ui.theme.PrimaryGradient // 🌟 Import Gradient Global

@Composable
fun CustomPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

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
        enabled = enabled,
        contentPadding = PaddingValues(), // 🌟 Hilangkan padding bawaan Button
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, // 🌟 Dibuat transparan agar Box di dalamnya terlihat
            contentColor = MaterialTheme.colorScheme.background,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.background
        )
    ) {
        // 🌟 Box ini yang akan memberikan warna Gradient secara penuh
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (enabled) PrimaryGradient else DisabledPrimaryGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun CustomOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

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
        enabled = enabled,
        border = BorderStroke(2.dp, if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = Color.Transparent,
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

@Composable
fun CustomBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            // margin kanan
            .padding(end = 12.dp)
            .size(40.dp)
            .glowShadow(
                color = MaterialTheme.colorScheme.onBackground,
                alpha = 0.8f,
                blurRadius = 2.dp,
                borderRadius = 20.dp
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape
            )
            .border(1.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Kembali",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun CustomCircularIconButton(
    icon: ImageVector,
    contentDescription: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 40.dp,
    iconSize: Dp = 20.dp,
    glowColor: Color = color,
    showGlow: Boolean = true
) {
    Box(
        modifier = modifier
            .size(buttonSize)
            .then(
                if (showGlow) {
                    Modifier.glowShadow(
                        color = glowColor,
                        alpha = 0.8f,
                        blurRadius = 2.dp,
                        borderRadius = (buttonSize.value / 2).dp
                    )
                } else {
                    Modifier
                }
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )
            .border(1.dp, color, CircleShape)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier.size(iconSize)
        )
    }
}