package com.acaris.features.profile.data.remote.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("npm_nip") val identifier: String?,
    @SerializedName("profile_picture") val profilePicture: String?,
    @SerializedName("angkatan") val angkatan: Int?,
    @SerializedName("ipk") val ipk: String?,
    @SerializedName("current_semester") val currentSemester: Int?,
    @SerializedName("nama_dosen_pa") val dosenPa: String?
)

data class PhotoUpdateResponse(
    @SerializedName("profile_picture") val profilePictureUrl: String?
)