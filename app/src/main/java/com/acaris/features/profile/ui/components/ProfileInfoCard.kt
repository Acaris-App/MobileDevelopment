package com.acaris.features.profile.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.features.profile.domain.model.UserProfile

@Composable
fun ProfileInfoCard(
    userProfile: UserProfile,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // Sudut melengkung sesuai wireframe
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar Placeholder (Nanti bisa diganti AsyncImage Coil kalau ada URL foto)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Data Lines
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

            // Tombol Edit di pojok kanan bawah
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 12.dp, y = 12.dp) // Digeser sedikit keluar sesuai wireframe
                    .border(2.dp, Color.Black, CircleShape)
                    .background(Color.White, CircleShape)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profil", tint = Color.Black)
            }
        }
    }
}

@Composable
fun ProfileDataLine(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}