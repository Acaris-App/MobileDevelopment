package com.acaris.features.schedule.data.mapper

import com.acaris.features.schedule.data.remote.model.ScheduleResponse
import com.acaris.features.schedule.domain.model.Schedule
import com.acaris.features.schedule.domain.model.StudentBooking

fun ScheduleResponse.toDomain(): Schedule {
    return Schedule(
        id = this.id.orEmpty(),
        dosenId = this.dosenId.orEmpty(),
        dosenName = this.dosenName ?: "Dosen",
        date = this.date?.substringBefore("T").orEmpty(),
        startTime = this.startTime.orEmpty(),
        endTime = this.endTime.orEmpty(),
        quota = this.quota ?: 0,
        remainingQuota = this.remainingQuota ?: 0,
        status = this.status ?: "Tersedia",
        keterangan = this.keterangan.orEmpty(),
        bookingId = this.bookingId,
        mahasiswaAgenda = this.mahasiswaAgenda,
        bookedStudents = this.bookedStudents?.map {
            StudentBooking(
                id = it.id?.toString().orEmpty(),
                nama = it.nama.orEmpty(),
                npm = it.npm.orEmpty(),
                keterangan = it.keterangan.orEmpty()
            )
        } ?: emptyList()
    )
}