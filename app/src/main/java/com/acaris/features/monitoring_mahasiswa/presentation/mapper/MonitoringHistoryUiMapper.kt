package com.acaris.features.monitoring_mahasiswa.presentation.mapper

import com.acaris.core.utils.DateUtils
import com.acaris.features.monitoring_mahasiswa.domain.model.RiwayatBimbingan
import com.acaris.features.monitoring_mahasiswa.presentation.model.RiwayatBimbinganUiModel

fun RiwayatBimbingan.toUiModel(): RiwayatBimbinganUiModel {
    val formattedDate = DateUtils.formatShortDateToIndo(this.date)

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