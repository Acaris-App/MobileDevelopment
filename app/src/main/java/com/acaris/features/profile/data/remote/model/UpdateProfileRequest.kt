package com.acaris.features.profile.data.remote.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("npm_nip") val identifier: String,
    @SerializedName("angkatan") val angkatan: Int? = null,
    @SerializedName("ipk") val ipk: Double? = null,
    @SerializedName("current_semester") val currentSemester: Int? = null
)