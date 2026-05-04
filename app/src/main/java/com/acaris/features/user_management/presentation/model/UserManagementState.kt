package com.acaris.features.user_management.presentation.model

data class UserManagementState(
    val isLoading: Boolean = false,
    val users: List<UserUiModel> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val currentRole: String = "mahasiswa",
    val currentSearch: String = "",
    val currentSortBy: String = "name_asc",
    val isActionLoading: Boolean = false
)