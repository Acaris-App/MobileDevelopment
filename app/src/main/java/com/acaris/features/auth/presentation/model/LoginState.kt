package com.acaris.features.auth.presentation.model

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: UserUiModel) : LoginState() // 🌟 Ganti di sini
    data class Error(val message: String) : LoginState()
}