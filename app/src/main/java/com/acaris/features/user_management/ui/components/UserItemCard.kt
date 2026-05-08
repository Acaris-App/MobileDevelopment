// File: ui/components/UserItemCard.kt
package com.acaris.features.user_management.ui.components

import androidx.compose.foundation.border // 🌟 IMPOR BORDER
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.features.user_management.presentation.model.UserUiModel

@Composable
fun UserItemCard(
    user: UserUiModel,
    onCardClick: (String) -> Unit,
    onEditClick: (UserUiModel) -> Unit,
    onDeleteClick: (UserUiModel) -> Unit,
    onStatusToggle: (UserUiModel, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isMahasiswa = user.role.lowercase() == "mahasiswa"
    val isAdmin = user.role.lowercase() == "admin"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = isMahasiswa,
                onClick = { onCardClick(user.id) }
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(50.dp)
                ) {
                    if (!user.profilePictureUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = user.profilePictureUrl,
                            contentDescription = "Foto Profil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = user.identifier,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = user.email,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                if (isMahasiswa) {
                    UserDetailRow("Angkatan", user.angkatan?.toString() ?: "-")
                    Spacer(modifier = Modifier.height(6.dp))

                    UserDetailRow("Semester", user.currentSemester?.toString() ?: "-")
                    Spacer(modifier = Modifier.height(6.dp))

                    UserDetailRow("Kelas", user.kodeKelas ?: "-")
                    Spacer(modifier = Modifier.height(6.dp))

                    UserDetailRow("IPK", user.ipk?.toString() ?: "-")
                    Spacer(modifier = Modifier.height(6.dp))

                    UserDetailRow("Dosen PA", user.dosenPa ?: "Belum Atur")
                    Spacer(modifier = Modifier.height(6.dp))

                    UserDetailRow("Total Bimbingan", "${user.totalBimbingan ?: 0} Kali")
                }
                else if (user.role.lowercase() == "dosen") {
                    UserDetailRow("Kelas Bimbingan", user.kodeKelas ?: "-")
                    Spacer(modifier = Modifier.height(6.dp))

                    UserDetailRow("Mahasiswa Bimbingan", "${user.totalMahasiswa ?: 0} Orang")
                    Spacer(modifier = Modifier.height(6.dp))

                    UserDetailRow("Total Sesi Bimbingan", "${user.totalBimbingan ?: 0} Sesi")
                }
                else {
                    UserDetailRow("Hak Akses", "Administrator Sistem")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (!isAdmin) Arrangement.SpaceBetween else Arrangement.End
            ) {

                if (!isAdmin) {
                    val statusColor = if (user.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    val statusText = if (user.isActive) "AKTIF" else "NONAKTIF"

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.1f),
                        modifier = Modifier.border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isAdmin) {
                        Switch(
                            checked = user.isActive,
                            onCheckedChange = { onStatusToggle(user, it) },
                            modifier = Modifier.scale(0.8f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    CustomCircularIconButton(
                        icon = Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = { onEditClick(user) }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CustomCircularIconButton(
                        icon = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = { onDeleteClick(user) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun UserDetailItem(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$label: ", fontSize = 13.sp, color = Color.Gray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}