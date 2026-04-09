package com.acaris.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.auth.domain.usecase.RequestForgotPasswordUseCase
import com.acaris.features.auth.domain.usecase.ResetPasswordUseCase
import com.acaris.features.auth.domain.usecase.VerifyResetPasswordOtpUseCase
import com.acaris.features.auth.presentation.model.ForgotPasswordState
import com.acaris.features.auth.presentation.model.ForgotPasswordStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val requestForgotPasswordUseCase: RequestForgotPasswordUseCase,
    private val verifyResetPasswordOtpUseCase: VerifyResetPasswordOtpUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordState())
    val uiState: StateFlow<ForgotPasswordState> = _uiState.asStateFlow()

    fun requestOtp(email: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = requestForgotPasswordUseCase(email)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isLoading = false, email = email, currentStep = ForgotPasswordStep.INPUT_OTP)
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun verifyOtp(otp: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val savedEmail = _uiState.value.email
            val result = verifyResetPasswordOtpUseCase(savedEmail, otp)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isLoading = false, otpCode = otp, currentStep = ForgotPasswordStep.INPUT_NEW_PASSWORD)
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun resetPassword(newPassword: String, confirmPassword: String) {
        if (newPassword != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Konfirmasi password tidak cocok.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val state = _uiState.value
            val result = resetPasswordUseCase(state.email, state.otpCode, newPassword)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, currentStep = ForgotPasswordStep.SUCCESS) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun navigateBack() {
        _uiState.update { state ->
            when (state.currentStep) {
                ForgotPasswordStep.INPUT_OTP -> state.copy(currentStep = ForgotPasswordStep.INPUT_EMAIL)
                ForgotPasswordStep.INPUT_NEW_PASSWORD -> state.copy(currentStep = ForgotPasswordStep.INPUT_OTP)
                else -> state
            }
        }
    }
}