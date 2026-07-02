package com.acaris.features.user_management.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Info
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
import com.acaris.core.ui.theme.StatusBookedText
import com.acaris.core.ui.theme.StatusSelesaiText
import com.acaris.core.utils.DateUtils
import com.acaris.features.user_management.presentation.model.BimbinganHistoryUiModel

val BimbinganHistoryUiModel.rawStatus: String get() = this.status ?: ""
val BimbinganHistoryUiModel.statusLabel: String get() = this.status?.uppercase() ?: "UNKNOWN"
val BimbinganHistoryUiModel.displayDate: String get() = DateUtils.formatIsoToIndo(this.date)
val BimbinganHistoryUiModel.displayTime: String get() = this.time ?: "-"

@Composable
fun BimbinganRiwayatSection(bimbinganHistory: List<BimbinganHistoryUiModel>) {
    if (bimbinganHistory.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Belum ada riwayat bimbingan.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(bimbinganHistory) { riwayat ->
                AdminRiwayatBimbinganCard(riwayat = riwayat)
            }
        }
    }
}

@Composable
private fun AdminRiwayatBimbinganCard(
    riwayat: BimbinganHistoryUiModel,
    modifier: Modifier = Modifier
) {
    val statusColor = if (riwayat.rawStatus.contains("selesai", ignoreCase = true)) StatusSelesaiText else StatusBookedText

    val ketDosen = riwayat.keteranganDosen?.ifBlank { "Tidak ada keterangan" } ?: "Tidak ada keterangan"
    val agendaText = riwayat.agenda ?: ""

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
                    text = riwayat.displayDate,
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
                        text = riwayat.statusLabel,
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
                Text(text = riwayat.displayTime, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    text = ketDosen,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }

            if (agendaText.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Agenda: $agendaText",
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