package com.acaris.features.schedule.domain.usecase

import com.acaris.features.schedule.domain.model.Schedule
import com.acaris.features.schedule.domain.repository.ScheduleRepository
import java.time.LocalTime
import javax.inject.Inject

class UpdateScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        id: String,
        date: String,
        startTime: String,
        endTime: String,
        quota: Int,
        keterangan: String
    ): Result<Schedule> {

        if (quota <= 0) {
            return Result.failure(Exception("Kuota harus lebih dari 0."))
        }

        if (startTime >= endTime) {
            return Result.failure(Exception("Waktu selesai harus lebih dari waktu mulai."))
        }

        val existingSchedulesResult = repository.getDailySchedules(date)
        if (existingSchedulesResult.isSuccess) {
            val existingSchedules = existingSchedulesResult.getOrNull() ?: emptyList()
            if (isTimeConflict(startTime, endTime, id, existingSchedules)) {
                return Result.failure(Exception("Jadwal gagal diperbarui: Waktu bertabrakan dengan jadwal bimbingan lain!"))
            }
        }

        return repository.updateSchedule(id, date, startTime, endTime, quota, keterangan)
    }

    private fun isTimeConflict(newStartStr: String, newEndStr: String, ignoreId: String, existingSchedules: List<Schedule>): Boolean {
        try {
            val newStart = LocalTime.parse(newStartStr)
            val newEnd = LocalTime.parse(newEndStr)

            return existingSchedules.any { schedule ->
                if (schedule.id == ignoreId) return@any false

                val existingStart = LocalTime.parse(schedule.startTime)
                val existingEnd = LocalTime.parse(schedule.endTime)
                newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)
            }
        } catch (e: Exception) {
            return true
        }
    }
}