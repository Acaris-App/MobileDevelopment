package com.acaris.features.schedule.data.remote.model

import com.google.gson.annotations.SerializedName

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
    @SerializedName("keterangan") val keterangan: String?
)