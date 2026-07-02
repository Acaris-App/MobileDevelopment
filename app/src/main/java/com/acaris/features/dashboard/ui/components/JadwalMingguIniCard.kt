package com.acaris.features.dashboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.components.glowShadow
import com.acaris.core.ui.theme.StatusAvailableText
import com.acaris.core.ui.theme.StatusBookedText
import com.acaris.core.ui.theme.StatusFullText
import com.acaris.core.ui.theme.StatusSelesaiText
import com.acaris.features.dashboard.presentation.model.JadwalMingguIniUiModel
import com.acaris.features.schedule.presentation.model.ScheduleStatus

@Composable
fun JadwalMingguIniCard(
    jadwal: JadwalMingguIniUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val (statusText, statusColor) = when (jadwal.status) {
        ScheduleStatus.SELESAI -> "SELESAI" to StatusSelesaiText
        ScheduleStatus.AVAILABLE -> "TERSEDIA" to StatusAvailableText
        ScheduleStatus.FULL -> "DIPESAN" to StatusFullText
        else -> "DIPESAN" to StatusBookedText
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .glowShadow(
                color = statusColor,
                alpha = 0.4f,
                blurRadius = 4.dp,
                borderRadius = 16.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, statusColor.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // HEADER: Tanggal, Waktu, & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    // 🌟 Tetap mempertahankan Hari & Tanggal di atas jam
                    Text(
                        text = jadwal.date,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = jadwal.waktu,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
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

            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (jadwal.keterangan.isNotBlank()) jadwal.keterangan else "Tidak ada keterangan",
                    fontSize = 14.sp,
                    color = if (jadwal.keterangan.isNotBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontStyle = if (jadwal.keterangan.isNotBlank()) FontStyle.Normal else FontStyle.Italic,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // DAFTAR MAHASISWA
            Text(
                text = "Daftar Mahasiswa (${jadwal.listMahasiswa.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (jadwal.listMahasiswa.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Belum ada mahasiswa yang mem-booking.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                jadwal.listMahasiswa.forEach { mahasiswa ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(0.5.dp, statusColor.copy(alpha = 0.8f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = mahasiswa.nama,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = mahasiswa.npm,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (mahasiswa.agenda.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = statusColor.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = "Agenda: ${mahasiswa.agenda}",
                                        fontSize = 12.sp,
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}