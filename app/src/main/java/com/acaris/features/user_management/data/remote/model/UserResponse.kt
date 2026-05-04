package com.acaris.features.user_management.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("identifier") val identifier: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("profile_picture_url") val profilePictureUrl: String?,

    // Data Mahasiswa
    @SerializedName("angkatan") val angkatan: Int?,
    @SerializedName("current_semester") val currentSemester: Int?,
    @SerializedName("dosen_pa") val dosenPa: String?,
    @SerializedName("kode_kelas") val kodeKelas: String?,
    @SerializedName("ipk") val ipk: String? = null,

    // Data Agregasi
    @SerializedName("total_bimbingan") val totalBimbingan: Int?,
    @SerializedName("total_mahasiswa") val totalMahasiswa: Int?
)