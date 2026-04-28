package com.acaris.features.schedule.data.repository

import com.acaris.core.network.parseApiError // 🌟 IMPORT PARSER-NYA DI SINI
import com.acaris.features.schedule.data.mapper.toDomain
import com.acaris.features.schedule.data.remote.datasource.ScheduleApiService
import com.acaris.features.schedule.data.remote.model.CreateScheduleRequest
import com.acaris.features.schedule.domain.model.Schedule
import com.acaris.features.schedule.domain.repository.ScheduleRepository
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val apiService: ScheduleApiService
) : ScheduleRepository {

    override suspend fun createSchedule(
        date: String,
        startTime: String,
        endTime: String,
        quota: Int,
        keterangan: String
    ): Result<Schedule> {
        return try {
            val request = CreateScheduleRequest(date, startTime, endTime, quota, keterangan)
            val response = apiService.createSchedule(request)

            if (response.status == "success" || response.status == "200") {
                val schedule = response.data?.toDomain() ?: throw Exception("Data jadwal kosong dari server")
                Result.success(schedule)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getMonthlySchedules(year: Int, month: Int): Result<List<Schedule>> {
        return try {
            val response = apiService.getMonthlySchedules(year, month)
            if (response.status == "success" || response.status == "200") {
                val schedules = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(schedules)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getDailySchedules(date: String): Result<List<Schedule>> {
        return try {
            val response = apiService.getDailySchedules(date)
            if (response.status == "success" || response.status == "200") {
                val schedules = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(schedules)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun deleteSchedule(id: String): Result<Boolean> {
        return try {
            val response = apiService.deleteSchedule(id)
            if (response.status == "success" || response.status == "200") {
                Result.success(true)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun updateSchedule(
        id: String, date: String, startTime: String, endTime: String, quota: Int, keterangan: String
    ): Result<Schedule> {
        return try {
            val request = CreateScheduleRequest(date, startTime, endTime, quota, keterangan)
            val response = apiService.updateSchedule(id, request)

            if (response.status == "success" || response.status == "200") {
                val schedule = response.data?.toDomain() ?: throw Exception("Data kosong")
                Result.success(schedule)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getMahasiswaMonthlySchedules(year: Int, month: Int): Result<List<Schedule>> {
        return try {
            val response = apiService.getMahasiswaMonthlySchedules(year, month)
            if (response.status == "success" || response.status == "200") {
                val schedules = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(schedules)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getMahasiswaDailySchedules(date: String): Result<List<Schedule>> {
        return try {
            val response = apiService.getMahasiswaDailySchedules(date)
            if (response.status == "success" || response.status == "200") {
                val schedules = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(schedules)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun bookSchedule(scheduleId: String, agenda: String): Result<Boolean> {
        return try {
            val request = com.acaris.features.schedule.data.remote.model.BookScheduleRequest(scheduleId, agenda)
            val response = apiService.bookSchedule(request)
            if (response.status == "success" || response.status == "200") {
                Result.success(true)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getMahasiswaBookingHistory(): Result<List<Schedule>> {
        return try {
            val response = apiService.getMahasiswaBookingHistory()
            if (response.status == "success" || response.status == "200") {
                val schedules = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(schedules)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }
}