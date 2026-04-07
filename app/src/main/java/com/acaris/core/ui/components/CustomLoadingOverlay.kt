package com.acaris.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CustomLoadingOverlay(
    isLoading: Boolean
) {
    if (isLoading) {
        // Menggunakan Dialog agar menutupi seluruh layar dan mengunci interaksi
        Dialog(
            onDismissRequest = { /* Kosongkan agar user tidak bisa menutup loading secara paksa */ },
            properties = DialogProperties(
                dismissOnBackPress = false, // Tidak bisa ditutup dengan tombol Back
                dismissOnClickOutside = false // Tidak bisa ditutup dengan klik area luar
            )
        ) {
            // Kotak putih/gelap melengkung di tengah layar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface, // Menyesuaikan mode terang/gelap
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                // 🌟 INI DIA ANIMASI LOADING MATERIAL 3
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary, // Warna biru Acaris-mu
                    trackColor = MaterialTheme.colorScheme.surfaceVariant, // Warna trek lintasan pudar
                    strokeWidth = 4.dp
                )
            }
        }
    }
}