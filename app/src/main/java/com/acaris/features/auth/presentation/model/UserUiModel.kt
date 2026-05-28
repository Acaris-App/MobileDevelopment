package com.acaris.features.auth.presentation.model

data class UserUiModel(
    val email: String,
    val name: String,
    val role: String,
    val kodeKelas: String? = null
)