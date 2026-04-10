package com.acaris.features.profile.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val identifier: String,
    val profilePictureUrl: String?,
    val angkatan: Int? = null,
    val ipk: Double? = null,
    val currentSemester: Int? = null,
    val dosenPa: String? = null
)