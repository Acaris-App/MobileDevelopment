package com.acaris.features.schedule.presentation.mapper

import com.acaris.features.schedule.domain.model.Schedule
import com.acaris.features.schedule.presentation.model.ScheduleUiModel
import com.acaris.features.schedule.presentation.model.StudentBookingUiModel

fun Schedule.toPresentation(): ScheduleUiModel {
    val isScheduleFull = this.remainingQuota <= 0
    val isPast = this.isExpired()

    val quotaInfoText = when {
        isPast -> "Sesi Telah Berakhir"
        isScheduleFull -> "Penuh (${this.quota}/${this.quota})"
        else -> "Sisa Kuota: ${this.remainingQuota}/${this.quota}"
    }
    val formatTime = { timeStr: String ->
        timeStr.split(":").take(2).joinToString(":")
    }

    return ScheduleUiModel(
        id = this.id,
        title = "Bimbingan Akademik",
        date = this.date,
        time = "${formatTime(this.startTime)} - ${formatTime(this.endTime)}",
        quotaInfo = quotaInfoText,
        status = if (isPast) "Selesai" else this.status.ifBlank { "Tersedia" },
        keterangan = this.keterangan.ifBlank { "Tidak ada keterangan" },
        rawStartTime = this.startTime,
        rawEndTime = this.endTime,
        rawQuota = this.quota,
        isFull = isScheduleFull || isPast,
        isSelesai = isPast,
        bookedStudents = this.bookedStudents.map {
            StudentBookingUiModel(
                id = it.id,
                nama = it.nama,
                npm = it.npm,
                keterangan = it.keterangan
            )
        }
    )
}