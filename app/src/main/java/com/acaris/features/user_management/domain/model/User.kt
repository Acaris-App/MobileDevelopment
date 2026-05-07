package com.acaris.features.user_management.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val identifier: String,
    val status: String,
    val profilePictureUrl: String?,
    val angkatan: Int?,
    val currentSemester: Int?,
    val dosenPa: String?,
    val kodeKelas: String?,
    val totalBimbingan: Int?,
    val totalMahasiswa: Int?,
    val ipk: Double?
)