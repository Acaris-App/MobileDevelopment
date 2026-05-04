package com.acaris.features.user_management.presentation.model

data class UserUiModel(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val identifier: String,
    val isActive: Boolean,
    val profilePictureUrl: String?,
    val angkatan: Int?,
    val currentSemester: Int?,
    val dosenPa: String?,
    val kodeKelas: String?,
    val totalBimbingan: Int?,
    val totalMahasiswa: Int?,
    val ipk: Double?
)