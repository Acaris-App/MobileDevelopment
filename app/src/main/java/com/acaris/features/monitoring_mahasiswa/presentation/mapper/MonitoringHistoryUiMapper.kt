package com.acaris.features.monitoring_mahasiswa.presentation.mapper

import com.acaris.features.monitoring_mahasiswa.domain.model.RiwayatBimbingan
import com.acaris.features.monitoring_mahasiswa.presentation.model.RiwayatBimbinganUiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun RiwayatBimbingan.toUiModel(): RiwayatBimbinganUiModel {
    val formattedDate = try {
        val dateObj = LocalDate.parse(this.date)
        dateObj.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("id", "ID")))
    } catch (e: Exception) {
        this.date
    }

    val isPast = this.isExpired()

    val label = when {
        isPast -> "Selesai"
        this.status.lowercase() == "selesai" -> "Selesai"
        this.status.lowercase() == "menunggu" -> "Dipesan"
        else -> this.status.replaceFirstChar { it.uppercase() }
    }

    val finalRawStatus = if (isPast || this.status.lowercase() == "selesai") "selesai" else "dipesan"

    return RiwayatBimbinganUiModel(
        id = this.id,
        displayDate = formattedDate,
        displayTime = this.time,
        agenda = this.agenda,
        rawStatus = finalRawStatus,
        statusLabel = label,
        keteranganDosen = this.keteranganDosen
    )
}