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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomImageZoomDialog
import com.acaris.core.ui.components.glowShadow
import com.acaris.features.monitoring_mahasiswa.presentation.model.DetailMahasiswaUiModel

@Composable
fun DetailProfilMahasiswaCard(
    detail: DetailMahasiswaUiModel,
    modifier: Modifier = Modifier
) {
    var showZoomedImage by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .glowShadow(
                color = MaterialTheme.colorScheme.primary,
                alpha = 0.35f,
                blurRadius = 8.dp,
                borderRadius = 16.dp
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                        .clip(CircleShape)
                        .clickable { showZoomedImage = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (detail.profilePictureUrl.isBlank()) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        AsyncImage(
                            model = detail.profilePictureUrl,
                            contentDescription = "Foto Profil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                MonitoringDataLine(label = "Nama", value = detail.name)
                MonitoringDataLine(label = "NPM", value = detail.npm)
                MonitoringDataLine(label = "Email", value = detail.email)

                MonitoringDataLine(label = "Angkatan", value = detail.angkatan)
                MonitoringDataLine(label = "Semester Saat Ini", value = detail.semester)

                MonitoringDataLine(label = "IPK", value = detail.ipk)
                MonitoringDataLine(label = "Kode Kelas", value = detail.kodeKelas)
            }
        }
    }

    if (showZoomedImage) {
        CustomImageZoomDialog(
            imageUrl = detail.profilePictureUrl,
            onDismissRequest = { showZoomedImage = false }
        )
    }
}

@Composable
private fun MonitoringDataLine(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}