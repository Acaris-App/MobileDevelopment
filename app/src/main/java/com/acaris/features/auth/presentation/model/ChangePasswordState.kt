package com.acaris.features.auth.presentation.model

data class ChangePasswordState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)