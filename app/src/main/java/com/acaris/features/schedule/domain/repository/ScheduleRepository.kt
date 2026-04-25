package com.acaris.features.schedule.domain.repository

import com.acaris.features.schedule.domain.model.Schedule

interface ScheduleRepository {
    suspend fun createSchedule(
        date: String,
        startTime: String,
        endTime: String,
        quota: Int,
        keterangan: String
    ): Result<Schedule>
    suspend fun getMonthlySchedules(year: Int, month: Int): Result<List<Schedule>>
    suspend fun getDailySchedules(date: String): Result<List<Schedule>>
    suspend fun deleteSchedule(id: String): Result<Boolean>
    suspend fun updateSchedule(id: String, date: String, startTime: String, endTime: String, quota: Int, keterangan: String): Result<Schedule>
    suspend fun getMahasiswaMonthlySchedules(year: Int, month: Int): Result<List<Schedule>>
    suspend fun getMahasiswaDailySchedules(date: String): Result<List<Schedule>>
    suspend fun bookSchedule(scheduleId: String, agenda: String): Result<Boolean>
    suspend fun getMahasiswaBookingHistory(): Result<List<Schedule>>
}