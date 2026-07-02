package com.acaris.features.profile.presentation.model

data class ProfileUiModel(
    val id: String,
    val name: String,
    val email: String,
    val identifier: String,
    val identifierLabel: String,
    val role: String,
    val displayRole: String,
    val profilePictureUrl: String,
    val angkatan: String,
    val currentSemester: String,
    val ipk: String,
    val dosenPa: String,
    val isMahasiswa: Boolean,

    val rawAngkatan: String,
    val rawSemester: String,
    val rawIpk: String
)

data class ProfileState(
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val profileData: ProfileUiModel? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    val name: String = "",
    val email: String = "",
    val identifier: String = "",
    val angkatan: String = "",
    val semester: String = "",
    val ipk: String = "",
    val isFormInitialized: Boolean = false
)