package com.acaris.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.auth.domain.usecase.*
import com.acaris.features.auth.presentation.mapper.toPresentation
import com.acaris.features.auth.presentation.model.RegisterState
import com.acaris.features.auth.presentation.model.RegisterStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateKodeKelasUseCase: ValidateKodeKelasUseCase,
    private val registerMahasiswaUseCase: RegisterMahasiswaUseCase,
    private val registerDosenUseCase: RegisterDosenUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val uploadDokumenUseCase: UploadDokumenUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()
    private var selectedProfilePictureFile: File? = null
    private var role: String = ""
    private var tempEmail: String = ""
    private var tempKodeKelas: String = ""

    var currentSemester: Int = 1
        private set

    fun initRole(selectedRole: String) {
        role = selectedRole.lowercase()
        val initialStep = if (role == "mahasiswa") RegisterStep.INPUT_KODE_KELAS else RegisterStep.INPUT_DATA_DIRI
        _uiState.update { it.copy(currentStep = initialStep) }
    }

    fun submitKodeKelas(kode: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = validateKodeKelasUseCase(kode)
            result.fold(
                onSuccess = {
                    tempKodeKelas = kode
                    _uiState.update { it.copy(isLoading = false, currentStep = RegisterStep.INPUT_DATA_DIRI) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun submitDataDiriMahasiswa(npm: String, name: String, email: String, password: String, angkatan: Int, semester: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        currentSemester = semester
        tempEmail = email

        viewModelScope.launch {
            val result = registerMahasiswaUseCase(npm, name, email, password, angkatan, semester, tempKodeKelas)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, currentStep = RegisterStep.INPUT_OTP) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun submitDataDiriDosen(nip: String, name: String, email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        tempEmail = email

        viewModelScope.launch {
            val result = registerDosenUseCase(nip, name, email, password)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, currentStep = RegisterStep.INPUT_OTP) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun submitOtp(otpCode: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = verifyOtpUseCase(tempEmail, otpCode)
            result.fold(
                onSuccess = { userDomain ->
                    val userPresentation = userDomain.toPresentation()

                    val nextStep = if (role == "mahasiswa") RegisterStep.UPLOAD_DOKUMEN else RegisterStep.SUCCESS_REGISTER

                    _uiState.update {
                        it.copy(isLoading = false, currentStep = nextStep, user = userPresentation)
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun uploadDokumen(documentType: String, file: File, documentSemester: Int?, onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = uploadDokumenUseCase(documentType, documentSemester, file)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun finishDocumentUpload() {
        _uiState.update { it.copy(currentStep = RegisterStep.SUCCESS_REGISTER) }
    }

    fun onProfilePictureSelected(file: File) {
        selectedProfilePictureFile = file
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}