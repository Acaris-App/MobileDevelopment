package com.acaris.features.dashboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.components.glowShadow
import com.acaris.core.ui.theme.StatusAvailableText
import com.acaris.core.ui.theme.StatusBookedText
import com.acaris.core.ui.theme.StatusFullText
import com.acaris.core.ui.theme.StatusSelesaiText
import com.acaris.features.dashboard.presentation.model.JadwalSingkatUiModel
import com.acaris.features.schedule.presentation.model.ScheduleStatus

@Composable
fun UpcomingBimbinganCard(
    jadwal: JadwalSingkatUiModel,
    modifier: Modifier = Modifier
) {
    val (statusText, statusColor) = when (jadwal.status) {
        ScheduleStatus.SELESAI -> "SELESAI" to StatusSelesaiText
        ScheduleStatus.BOOKED_BY_ME -> "DIPESAN" to StatusBookedText
        ScheduleStatus.FULL -> "PENUH" to StatusFullText
        ScheduleStatus.AVAILABLE -> "TERSEDIA" to StatusAvailableText
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
                    text = jadwal.date,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = jadwal.waktu,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Info, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (jadwal.keterangan.isNotBlank()) jadwal.keterangan else "Tidak ada catatan dari dosen.",
                    fontSize = 14.sp,
                    color = if (jadwal.keterangan.isNotBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontStyle = if (jadwal.keterangan.isNotBlank()) FontStyle.Normal else FontStyle.Italic,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Agenda Anda:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = statusColor.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = jadwal.agenda,
                        fontSize = 12.sp,
                        color = statusColor,
                        fontStyle = FontStyle.Normal
                    )
                }
            }
        }
    }
}