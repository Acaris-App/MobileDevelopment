package com.acaris.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.profile.domain.usecase.GetProfileUseCase
import com.acaris.features.profile.domain.usecase.UpdatePhotoUseCase
import com.acaris.features.profile.domain.usecase.UpdateProfileUseCase
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
    private val updatePhotoUseCase: UpdatePhotoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = getProfileUseCase()
            result.fold(
                onSuccess = { profile ->
                    _uiState.update { it.copy(isLoading = false, userProfile = profile) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        identifier: String,
        angkatan: Int? = null,
        ipk: Double? = null,
        semester: Int? = null
    ) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            val result = updateProfileUseCase(name, email, identifier, angkatan, ipk, semester)
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