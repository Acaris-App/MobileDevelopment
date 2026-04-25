package com.acaris.features.schedule.data.remote.datasource

import com.acaris.core.network.model.BaseResponse
import com.acaris.features.schedule.data.remote.model.BookScheduleRequest
import com.acaris.features.schedule.data.remote.model.CreateScheduleRequest
import com.acaris.features.schedule.data.remote.model.ScheduleResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ScheduleApiService {

    @POST("schedule")
    suspend fun createSchedule(
        @Body request: CreateScheduleRequest
    ): BaseResponse<ScheduleResponse>

    @GET("schedule/monthly")
    suspend fun getMonthlySchedules(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): BaseResponse<List<ScheduleResponse>>

    @GET("schedule/daily")
    suspend fun getDailySchedules(
        @Query("date") date: String
    ): BaseResponse<List<ScheduleResponse>>

    @retrofit2.http.DELETE("schedule/{id}")
    suspend fun deleteSchedule(
        @retrofit2.http.Path("id") id: String
    ): BaseResponse<Any>

    @retrofit2.http.PUT("schedule/{id}")
    suspend fun updateSchedule(
        @retrofit2.http.Path("id") id: String,
        @retrofit2.http.Body request: CreateScheduleRequest
    ): BaseResponse<ScheduleResponse>

    @GET("schedule/mahasiswa/monthly")
    suspend fun getMahasiswaMonthlySchedules(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): BaseResponse<List<ScheduleResponse>>

    @GET("schedule/mahasiswa/daily")
    suspend fun getMahasiswaDailySchedules(
        @Query("date") date: String
    ): BaseResponse<List<ScheduleResponse>>

    @POST("schedule/mahasiswa/book")
    suspend fun bookSchedule(
        @Body request: BookScheduleRequest
    ): BaseResponse<Any>

    @GET("schedule/mahasiswa/bookings/history")
    suspend fun getMahasiswaBookingHistory(): BaseResponse<List<ScheduleResponse>>
}