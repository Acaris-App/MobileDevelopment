package com.acaris.features.user_management.data.remote.model

import com.google.gson.annotations.SerializedName

data class BimbinganHistoryResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("time") val time: String?,
    @SerializedName("agenda") val agenda: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("keterangan") val keteranganDosen: String?
)