package com.acaris.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

@Composable
fun CustomImageZoomDialog(
    imageUrl: String?,
    onDismissRequest: () -> Unit,
    fallbackIcon: ImageVector = Icons.Default.Person,
    contentDescription: String = "Zoomed Image"
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable { onDismissRequest() }, // Klik area kosong untuk tutup
            contentAlignment = Alignment.Center
        ) {

            if (imageUrl.isNullOrEmpty()) {
                Icon(
                    imageVector = fallbackIcon,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f) // 🌟 BIKIN KOTAK 1:1
                        .padding(48.dp), // Beri padding sedikit agar ikon tidak mentok layar
                    tint = Color.White
                )
            } else {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = contentDescription,
                    // 🌟 FIX UTAMA: Pakai fillMaxWidth dan aspectRatio(1f) agar jadi bujur sangkar sempurna
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    // 🌟 Gunakan Crop agar gambar memenuhi kotak 1:1 tersebut (persis gaya foto profil WA)
                    contentScale = ContentScale.Crop
                )
            }

            // Tombol Silang di Kanan Atas
            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 32.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}