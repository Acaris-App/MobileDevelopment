package com.acaris.features.monitoring_mahasiswa.domain.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class RiwayatBimbingan(
    val id: String,
    val date: String,
    val time: String,
    val agenda: String,
    val status: String,
    val keteranganDosen: String
) {
    fun isExpired(): Boolean {
        return try {
            val today = LocalDate.now()
            val currentTime = LocalTime.now()
            val scheduleDate = LocalDate.parse(date)

            when {
                scheduleDate.isBefore(today) -> true
                scheduleDate.isEqual(today) -> {
                    // Ambil jam selesai dari range "19:27 - 19:28"
                    val endTimeStr = time.split(" - ").lastOrNull() ?: return false
                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    val endLocalTime = LocalTime.parse(endTimeStr, timeFormatter)
                    currentTime.isAfter(endLocalTime)
                }
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }
}