package com.acaris.features.schedule.data.remote.model

import com.google.gson.annotations.SerializedName

data class StudentBookingResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("nama") val nama: String?,
    @SerializedName("npm") val npm: String?,
    @SerializedName("keterangan") val keterangan: String?
)

data class CreateScheduleRequest(
    @SerializedName("date") val date: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("quota") val quota: Int,
    @SerializedName("keterangan") val keterangan: String
)

data class ScheduleResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("dosen_id") val dosenId: String?,
    @SerializedName("dosen_name") val dosenName: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("start_time") val startTime: String?,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("quota") val quota: Int?,
    @SerializedName("remaining_quota") val remainingQuota: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("keterangan") val keterangan: String?,
    @SerializedName("booking_id") val bookingId: String?,
    @SerializedName("mahasiswa_agenda") val mahasiswaAgenda: String?,
    @SerializedName("booked_students") val bookedStudents: List<StudentBookingResponse>? = null
)

data class BookScheduleRequest(
    @SerializedName("schedule_id") val scheduleId: String,
    @SerializedName("agenda") val agenda: String
)