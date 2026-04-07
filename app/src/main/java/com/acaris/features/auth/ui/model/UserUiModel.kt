package com.acaris.features.auth.ui.model

data class UserUiModel(
    val name: String,
    val role: String,
    val kodeKelas: String? = null
)