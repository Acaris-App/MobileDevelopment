package com.acaris.features.user_management.data.remote.model

import com.google.gson.annotations.SerializedName

data class ClassInfoResponse(
    @SerializedName("kode_kelas") val kodeKelas: String?,
    @SerializedName("dosen_pa") val dosenPa: String?
)