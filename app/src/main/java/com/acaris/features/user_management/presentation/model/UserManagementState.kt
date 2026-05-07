package com.acaris.features.user_management.presentation.model

data class UserManagementState(
    val isLoading: Boolean = false,
    val isAppending: Boolean = false,
    val isLastPage: Boolean = false,
    val currentPage: Int = 1,

    val users: List<UserUiModel> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val currentRole: String = "mahasiswa",
    val currentSearch: String = "",
    val currentSortBy: String = "name_asc",
    val isActionLoading: Boolean = false
)