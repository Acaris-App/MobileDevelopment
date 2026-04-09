package com.acaris.features.auth.presentation.model

enum class ForgotPasswordStep {
    INPUT_EMAIL,
    INPUT_OTP,
    INPUT_NEW_PASSWORD,
    SUCCESS
}

data class ForgotPasswordState(
    val currentStep: ForgotPasswordStep = ForgotPasswordStep.INPUT_EMAIL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val otpCode: String = ""
)