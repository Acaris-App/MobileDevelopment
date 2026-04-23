package com.acaris.features.schedule.domain.usecase

import com.acaris.features.schedule.domain.model.Schedule
import com.acaris.features.schedule.domain.repository.ScheduleRepository
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class CreateScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        date: String,
        startTime: String,
        endTime: String,
        quota: Int,
        keterangan: String
    ): Result<Schedule> {

        if (isDateInThePast(date, startTime)) {
            return Result.failure(Exception("Tidak dapat membuat jadwal di waktu yang sudah lewat."))
        }

        if (quota <= 0) {
            return Result.failure(Exception("Kuota harus lebih dari 0."))
        }

        if (startTime >= endTime) {
            return Result.failure(Exception("Waktu selesai harus lebih dari waktu mulai."))
        }

        val existingSchedulesResult = repository.getDailySchedules(date)
        if (existingSchedulesResult.isSuccess) {
            val existingSchedules = existingSchedulesResult.getOrNull() ?: emptyList()
            if (isTimeConflict(startTime, endTime, existingSchedules)) {
                return Result.failure(Exception("Jadwal gagal dibuat: Waktu bertabrakan dengan jadwal bimbingan lain!"))
            }
        }

        return repository.createSchedule(date, startTime, endTime, quota, keterangan)
    }

    private fun isTimeConflict(newStartStr: String, newEndStr: String, existingSchedules: List<Schedule>): Boolean {
        try {
            val newStart = LocalTime.parse(newStartStr)
            val newEnd = LocalTime.parse(newEndStr)

            return existingSchedules.any { schedule ->
                val existingStart = LocalTime.parse(schedule.startTime)
                val existingEnd = LocalTime.parse(schedule.endTime)
                newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)
            }
        } catch (e: Exception) {
            return true
        }
    }

    private fun isDateInThePast(dateStr: String, timeStr: String): Boolean {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val scheduleDate = format.parse("$dateStr $timeStr")
            val currentDate = Date()
            scheduleDate?.before(currentDate) ?: true
        } catch (e: Exception) {
            false
        }
    }
}