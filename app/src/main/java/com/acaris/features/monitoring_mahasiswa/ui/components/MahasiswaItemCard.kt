package com.acaris.features.monitoring_mahasiswa.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomImageZoomDialog // 🌟 IMPORT KOMPONEN ZOOM
import com.acaris.core.ui.components.glowShadow
import com.acaris.features.monitoring_mahasiswa.presentation.model.MahasiswaBimbinganUiModel

@Composable
fun MahasiswaItemCard(
    mahasiswa: MahasiswaBimbinganUiModel,
    onClick: () -> Unit
) {
    // 🌟 STATE BARU: Untuk melacak status Zoom Gambar
    var showZoomedImage by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .glowShadow(
                color = MaterialTheme.colorScheme.primary,
                alpha = 0.5f,
                blurRadius = 4.dp,
                borderRadius = 16.dp
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { showZoomedImage = true }, // 🌟 AKSI KLIK FOTO
                contentAlignment = Alignment.Center
            ) {
                if (mahasiswa.profilePictureUrl.isNullOrBlank()) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Avatar Default",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    AsyncImage(
                        model = mahasiswa.profilePictureUrl,
                        contentDescription = "Foto Profil ${mahasiswa.name}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mahasiswa.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mahasiswa.npm,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mahasiswa.infoAkademik,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }

    // 🌟 PANGGIL KOMPONEN DIALOG MENGGUNAKAN BLOK IF
    if (showZoomedImage) {
        CustomImageZoomDialog(
            imageUrl = mahasiswa.profilePictureUrl,
            onDismissRequest = { showZoomedImage = false }
        )
    }
}