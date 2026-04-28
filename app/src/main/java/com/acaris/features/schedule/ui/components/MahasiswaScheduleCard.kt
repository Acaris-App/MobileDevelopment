package com.acaris.features.schedule.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info // 🌟 Tambahan Import Icon Info
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
import com.acaris.core.ui.theme.StatusAvailableText
import com.acaris.core.ui.theme.StatusBookedText
import com.acaris.core.ui.theme.StatusFullText
import com.acaris.core.ui.theme.StatusSelesaiText
import com.acaris.features.schedule.presentation.model.ScheduleUiModel

@Composable
fun MahasiswaScheduleCard(
    schedule: ScheduleUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when {
        schedule.isSelesai -> StatusSelesaiText
        schedule.isBookedByMe -> StatusBookedText
        schedule.isFull -> StatusFullText
        else -> StatusAvailableText
    }

    val borderWidth = if (schedule.isBookedByMe) 2.5.dp else 1.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !schedule.isFull && !schedule.isBookedByMe) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(borderWidth, statusColor.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = schedule.title,
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
                        text = schedule.status,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = schedule.dosenName, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = schedule.time, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = schedule.keterangan,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            if (schedule.isBookedByMe && !schedule.myAgenda.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.Top) {
                    Column {
                        Text(text = "Agenda:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
                        Text(text = schedule.myAgenda, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                Text(
                    text = schedule.quotaInfo,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (schedule.isFull || schedule.isSelesai) MaterialTheme.colorScheme.onSurfaceVariant else statusColor
                )
            }
        }
    }
}