package com.acaris.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.core.domain.usecase.CalculateSemesterUseCase
import com.acaris.features.profile.domain.usecase.GetProfileUseCase
import com.acaris.features.profile.domain.usecase.UpdatePhotoUseCase
import com.acaris.features.profile.domain.usecase.UpdateProfileUseCase
import com.acaris.features.profile.presentation.mapper.toUiModel // 🌟 IMPORT MAPPER
import com.acaris.features.profile.presentation.model.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val updatePhotoUseCase: UpdatePhotoUseCase,
    private val calculateSemesterUseCase: CalculateSemesterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun onNameChanged(v: String) { _uiState.update { it.copy(name = v) } }
    fun onIdentifierChanged(v: String) { _uiState.update { it.copy(identifier = v) } }
    fun onIpkChanged(v: String) { _uiState.update { it.copy(ipk = v) } }

    fun onAngkatanChanged(newAngkatan: String) {
        _uiState.update { it.copy(angkatan = newAngkatan) }
        val finalSemester = calculateSemesterUseCase(newAngkatan)
        if (finalSemester.isNotEmpty()) {
            _uiState.update { it.copy(semester = finalSemester) }
        }
    }

    fun loadProfile() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = getProfileUseCase()
            result.fold(
                onSuccess = { profile ->
                    val uiModel = profile.toUiModel() // 🌟 PANGGIL MAPPER DI SINI
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profileData = uiModel,
                            name = uiModel.name,
                            email = uiModel.email,
                            identifier = uiModel.identifier,
                            angkatan = uiModel.rawAngkatan,
                            semester = uiModel.rawSemester,
                            ipk = uiModel.rawIpk,
                            isFormInitialized = true
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun updateProfile() {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            val result = updateProfileUseCase(
                name = currentState.name,
                email = currentState.email,
                identifier = currentState.identifier,
                angkatan = currentState.angkatan.toIntOrNull(),
                ipk = currentState.ipk.toDoubleOrNull(),
                semester = currentState.semester.toIntOrNull()
            )
            result.fold(
                onSuccess = {
                    loadProfile()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Data profil berhasil diperbarui!"
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun updateProfilePhoto(photoFile: File) {
        _uiState.update { it.copy(isUploadingPhoto = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            val result = updatePhotoUseCase(photoFile)
            result.fold(
                onSuccess = {
                    loadProfile()
                    _uiState.update {
                        it.copy(
                            isUploadingPhoto = false,
                            successMessage = "Foto profil berhasil diubah!"
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isUploadingPhoto = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}