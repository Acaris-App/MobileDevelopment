package com.acaris.features.user_management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import com.acaris.core.domain.usecase.CalculateSemesterUseCase
import com.acaris.features.user_management.domain.usecase.UpdateUserUseCase
import com.acaris.features.user_management.presentation.mapper.toUiModel
import com.acaris.features.user_management.presentation.model.EditUserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class EditUserViewModel @Inject constructor(
    private val repository: UserManagementRepository,
    private val updateUserUseCase: UpdateUserUseCase,
    private val calculateSemesterUseCase: CalculateSemesterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUserState())
    val uiState: StateFlow<EditUserState> = _uiState.asStateFlow()
    fun onNameChanged(newValue: String) { _uiState.update { it.copy(name = newValue) } }
    fun onEmailChanged(newValue: String) { _uiState.update { it.copy(email = newValue) } }
    fun onIdentifierChanged(newValue: String) { _uiState.update { it.copy(identifier = newValue) } }
    fun onIpkChanged(newValue: String) { _uiState.update { it.copy(ipk = newValue) } }
    fun onSemesterChanged(newValue: String) { _uiState.update { it.copy(semester = newValue) } }

    fun onClassSelected(kodeKelas: String, dosenPa: String) {
        _uiState.update { it.copy(kodeKelas = kodeKelas, dosenPa = dosenPa) }
    }

    fun onAngkatanChanged(newAngkatan: String) {
        _uiState.update { it.copy(angkatan = newAngkatan) }

        val finalSemester = calculateSemesterUseCase(newAngkatan)

        if (finalSemester.isNotEmpty()) {
            _uiState.update { it.copy(semester = finalSemester) }
        }
    }

    fun loadClasses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingClasses = true) }
            repository.getAllClasses().fold(
                onSuccess = { list -> _uiState.update { it.copy(availableClasses = list, isLoadingClasses = false) } },
                onFailure = { _uiState.update { it.copy(isLoadingClasses = false) } }
            )
        }
    }

    fun loadInitialData(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getUserDetail(userId).fold(
                onSuccess = { user ->
                    val uiModel = user.toUiModel()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            initialUser = uiModel,
                            role = uiModel.role.lowercase(),
                            name = uiModel.name,
                            email = uiModel.email,
                            identifier = uiModel.identifier,
                            angkatan = uiModel.angkatan?.toString() ?: "",
                            semester = uiModel.currentSemester?.toString() ?: "",
                            ipk = uiModel.ipk?.toString() ?: "",
                            dosenPa = uiModel.dosenPa ?: "",
                            kodeKelas = uiModel.kodeKelas ?: "",
                            isFormInitialized = true
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun updateUser(userId: String, profilePicture: File?) {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            val result = updateUserUseCase(
                id = userId,
                name = currentState.name,
                email = currentState.email,
                identifier = currentState.identifier,
                angkatan = currentState.angkatan.toIntOrNull(),
                currentSemester = currentState.semester.toIntOrNull(),
                dosenPa = currentState.dosenPa.takeIf { it.isNotBlank() },
                kodeKelas = currentState.kodeKelas.takeIf { it.isNotBlank() },
                ipk = currentState.ipk.toDoubleOrNull(),
                profilePicture = profilePicture
            )

            result.fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, successMessage = "Data pengguna berhasil diperbarui!") } },
                onFailure = { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}