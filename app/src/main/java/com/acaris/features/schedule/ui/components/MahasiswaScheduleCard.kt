package com.acaris.features.schedule.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.theme.StatusAvailableText
import com.acaris.core.ui.theme.StatusBookedBg
import com.acaris.core.ui.theme.StatusBookedText
import com.acaris.core.ui.theme.StatusFullBg
import com.acaris.core.ui.theme.StatusFullText
import com.acaris.core.ui.theme.StatusSelesaiBg
import com.acaris.core.ui.theme.StatusSelesaiText
import com.acaris.features.schedule.presentation.model.ScheduleUiModel

@Composable
fun MahasiswaScheduleCard(
    schedule: ScheduleUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusColor, containerColor) = when {
        schedule.isSelesai -> Pair(StatusSelesaiText, StatusSelesaiBg) // Kuning (Selesai)
        schedule.isBookedByMe -> Pair(StatusBookedText, StatusBookedBg) // Biru (Dipesan)
        schedule.isFull -> Pair(StatusFullText, StatusFullBg) // Merah (Penuh)
        else -> Pair(StatusAvailableText, MaterialTheme.colorScheme.surface) // Hijau (Tersedia)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !schedule.isFull && !schedule.isBookedByMe) { onClick() }
            .border(
                width = if (schedule.isBookedByMe) 2.dp else 0.dp,
                color = if (schedule.isBookedByMe) statusColor else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
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
                    modifier = Modifier.border(1.dp, statusColor, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = schedule.status,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Dosen: ${schedule.dosenName}", fontSize = 14.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = schedule.time, fontSize = 14.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            if (schedule.isBookedByMe && !schedule.myAgenda.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Agenda Anda:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
                        Text(text = schedule.myAgenda, fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
            } else {
                Text(
                    text = schedule.quotaInfo,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (schedule.isFull) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}