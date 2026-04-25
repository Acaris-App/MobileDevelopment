package com.acaris.features.schedule.presentation.mapper

import com.acaris.features.schedule.domain.model.Schedule
import com.acaris.features.schedule.presentation.model.ScheduleUiModel
import com.acaris.features.schedule.presentation.model.StudentBookingUiModel

fun Schedule.toMahasiswaPresentation(): ScheduleUiModel {
    val isBooked = this.bookingId != null
    val isScheduleFull = this.remainingQuota <= 0
    val isPast = this.isExpired()

    val quotaInfoText = when {
        isPast -> "Sesi Telah Berakhir"
        isBooked -> "Sudah Di-booking"
        isScheduleFull -> "Penuh (${this.quota}/${this.quota})"
        else -> "Sisa Kuota: ${this.remainingQuota}/${this.quota}"
    }

    return ScheduleUiModel(
        id = this.id,
        title = "Bimbingan - ${this.dosenName}",
        date = this.date,
        time = "${this.startTime} - ${this.endTime}",
        quotaInfo = quotaInfoText,
        status = when {
            isPast -> "Selesai"
            isBooked -> "Dipesan"
            else -> this.status.ifBlank { "Tersedia" }
        },
        keterangan = this.keterangan.ifBlank { "Tidak ada keterangan" },
        rawStartTime = this.startTime,
        rawEndTime = this.endTime,
        rawQuota = this.quota,
        isFull = isScheduleFull || isPast,
        dosenName = this.dosenName,
        isBookedByMe = isBooked,
        bookingId = this.bookingId,
        myAgenda = this.mahasiswaAgenda,
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