package com.acaris.features.schedule.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.core.ui.components.glowShadow
import com.acaris.core.ui.theme.StatusAvailableText
import com.acaris.core.ui.theme.StatusFullText
import com.acaris.core.ui.theme.StatusSelesaiText
import com.acaris.features.schedule.presentation.model.StudentBookingUiModel

@Composable
fun ScheduleDetailCard(
    timeSpan: String,
    quotaInfo: String,
    keteranganDosen: String,
    isFull: Boolean,
    isSelesai: Boolean,
    bookedStudents: List<StudentBookingUiModel>,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val statusColor = when {
        isSelesai -> StatusSelesaiText
        isFull -> StatusFullText
        else -> StatusAvailableText
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .glowShadow(
                color = statusColor,
                alpha = 0.4f,
                blurRadius = 4.dp,
                borderRadius = 16.dp
            )
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeSpan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.1f),
                        modifier = Modifier.border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = quotaInfo,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (!isSelesai) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CustomCircularIconButton(
                            icon = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            color = MaterialTheme.colorScheme.primary,
                            onClick = onEditClick
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        CustomCircularIconButton(
                            icon = Icons.Outlined.Delete,
                            contentDescription = "Hapus",
                            color = MaterialTheme.colorScheme.error,
                            onClick = onDeleteClick
                        )
                    }
                }
            }

            if (keteranganDosen.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = keteranganDosen,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Daftar Mahasiswa (${bookedStudents.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (bookedStudents.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Belum ada mahasiswa yang mem-booking.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                bookedStudents.forEach { student ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = BorderStroke(0.5.dp, statusColor.copy(alpha = 0.8f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = student.nama,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = student.npm,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (student.keterangan.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = statusColor.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = "Agenda: ${student.keterangan}",
                                        fontSize = 12.sp,
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontStyle = FontStyle.Normal
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