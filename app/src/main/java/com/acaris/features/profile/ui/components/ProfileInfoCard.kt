package com.acaris.features.profile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomImageZoomDialog
import com.acaris.core.ui.components.glowShadow // 🌟 IMPORT GLOW SHADOW
import com.acaris.features.profile.domain.model.UserProfile

@Composable
fun ProfileInfoCard(
    userProfile: UserProfile,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FOTO PROFIL KECIL (Sudah dibersihkan dari bayangan)
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .clickable { showZoomedImage = true },
                contentAlignment = Alignment.Center
            ) {
                if (userProfile.profilePictureUrl.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    AsyncImage(
                        model = userProfile.profilePictureUrl,
                        contentDescription = "Foto Profil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            ProfileDataLine(label = "Nama", value = userProfile.name)
            ProfileDataLine(label = "Email", value = userProfile.email)
            ProfileDataLine(
                label = if (userProfile.role == "mahasiswa") "NPM" else "NIP",
                value = userProfile.identifier
            )
            ProfileDataLine(label = "Peran", value = userProfile.role.replaceFirstChar { it.uppercase() })

            if (userProfile.role == "mahasiswa") {
                ProfileDataLine(label = "Angkatan", value = userProfile.angkatan?.toString() ?: "-")
                ProfileDataLine(label = "Semester Saat Ini", value = userProfile.currentSemester?.toString() ?: "-")
                ProfileDataLine(label = "IPK", value = userProfile.ipk?.toString() ?: "-")
                ProfileDataLine(label = "Dosen PA", value = userProfile.dosenPa ?: "-")
            }
        }
    }

    if (showZoomedImage) {
        CustomImageZoomDialog(
            imageUrl = userProfile.profilePictureUrl,
            onDismissRequest = { showZoomedImage = false }
        )
    }
}

@Composable
fun ProfileDataLine(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}