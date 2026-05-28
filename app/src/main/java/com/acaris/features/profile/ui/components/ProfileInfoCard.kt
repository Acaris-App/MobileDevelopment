package com.acaris.features.profile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
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
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.features.profile.domain.model.UserProfile

@Composable
fun ProfileInfoCard(
    userProfile: UserProfile,
    onEditClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                        .clip(CircleShape),
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

                Spacer(modifier = Modifier.height(24.dp))

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

            // TOMBOL 3 TITIK DAN DROPDOWN MENU
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = (-12).dp)
            ) {
                CustomCircularIconButton(
                    icon = Icons.Default.MoreVert,
                    contentDescription = "Opsi",
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { expanded = true },
                    modifier = Modifier.size(40.dp)
                )

                // FIX LENGKUNGAN DROPDOWN DI SINI
                // Membungkus menu dengan MaterialTheme untuk memaksa bentuk Popup-nya menjadi 16.dp
                MaterialTheme(
                    shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
                ) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Profil") },
                            onClick = {
                                expanded = false
                                onEditClick()
                            },
                            leadingIcon = {
                                CustomCircularIconButton(
                                    icon = Icons.Default.Edit,
                                    contentDescription = "Edit Profil",
                                    color = MaterialTheme.colorScheme.primary,
                                    onClick = {
                                        expanded = false
                                        onEditClick()
                                    },
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ganti Password") },
                            onClick = {
                                expanded = false
                                onChangePasswordClick()
                            },
                            leadingIcon = {
                                CustomCircularIconButton(
                                    icon = Icons.Default.Lock,
                                    contentDescription = "Ganti Password",
                                    color = MaterialTheme.colorScheme.primary,
                                    onClick = {
                                        expanded = false
                                        onChangePasswordClick()
                                    },
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        )
                    }
                }
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
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}