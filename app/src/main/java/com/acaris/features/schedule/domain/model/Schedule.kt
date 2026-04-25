package com.acaris.features.schedule.domain.model

import java.time.LocalDate

data class StudentBooking(
    val id: String,
    val nama: String,
    val npm: String,
    val keterangan: String
)

data class Schedule(
    val id: String,
    val dosenId: String,
    val dosenName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val quota: Int,
    val remainingQuota: Int,
    val status: String,
    val keterangan: String,
    val bookingId: String? = null,
    val mahasiswaAgenda: String? = null,
    val bookedStudents: List<StudentBooking> = emptyList()
) {
    fun isExpired(): Boolean {
        return try {
            LocalDate.parse(date).isBefore(LocalDate.now())
        } catch (e: Exception) {
            false
        }
    }
}