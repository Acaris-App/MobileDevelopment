package com.acaris.features.schedule.domain.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
            val today = LocalDate.now()
            val currentTime = LocalTime.now()
            val scheduleDate = LocalDate.parse(date)
            when {
                scheduleDate.isBefore(today) -> true
                scheduleDate.isEqual(today) -> {
                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                    val endLocalTime = LocalTime.parse(endTime, timeFormatter)
                    currentTime.isAfter(endLocalTime)
                }
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }
}