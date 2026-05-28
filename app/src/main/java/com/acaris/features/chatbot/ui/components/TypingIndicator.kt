package com.acaris.features.chatbot.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    val dots = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    // Animasi bergerak berulang-ulang
    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            // Memberi jeda (delay) antar titik agar loncatnya bergantian
            delay(index * 150L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 with LinearOutSlowInEasing     // Posisi awal (Bawah)
                        1.0f at 300 with FastOutLinearInEasing   // Posisi puncak (Atas)
                        0.0f at 600 with LinearOutSlowInEasing   // Kembali ke bawah
                        0.0f at 1200                             // Jeda sejenak sebelum loncat lagi
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    val dys = dots.map { it.value }
    val travelDistance = 6.dp // Tinggi loncatan titik

    Row(
        modifier = modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        dys.forEach { dy ->
            Box(
                modifier = Modifier
                    .size(6.dp) // Ukuran titik
                    .offset(y = -travelDistance * dy) // Efek loncat ke atas
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
            )
        }
    }
}