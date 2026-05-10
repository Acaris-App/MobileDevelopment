package com.acaris.features.user_management.presentation.model

import com.acaris.features.user_management.domain.model.ClassInfo

data class EditUserState(
    val isLoading: Boolean = false,
    val initialUser: UserUiModel? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val availableClasses: List<ClassInfo> = emptyList(),
    val isLoadingClasses: Boolean = false,

    val role: String = "",
    val name: String = "",
    val email: String = "",
    val identifier: String = "",
    val angkatan: String = "",
    val semester: String = "",
    val ipk: String = "",
    val dosenPa: String = "",
    val kodeKelas: String = "",
    val isFormInitialized: Boolean = false
)