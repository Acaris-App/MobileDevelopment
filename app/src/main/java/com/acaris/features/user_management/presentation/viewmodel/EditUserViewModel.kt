package com.acaris.features.user_management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import com.acaris.features.user_management.domain.usecase.UpdateUserUseCase
import com.acaris.features.user_management.presentation.mapper.toUiModel
import com.acaris.features.user_management.presentation.model.UserUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class EditUserState(
    val isLoading: Boolean = false,
    val initialUser: UserUiModel? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val availableClasses: List<String> = emptyList(), // 🌟 Daftar kelas untuk dropdown murni dari API
    val isLoadingClasses: Boolean = false
)

@HiltViewModel
class EditUserViewModel @Inject constructor(
    private val repository: UserManagementRepository,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUserState())
    val uiState: StateFlow<EditUserState> = _uiState.asStateFlow()

    // 🌟 FUNGSI BARU: Mengambil daftar kelas murni dari Backend
    fun loadClasses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingClasses = true) }
            repository.getAllClasses().fold(
                onSuccess = { list ->
                    _uiState.update { it.copy(availableClasses = list, isLoadingClasses = false) }
                },
                onFailure = {
                    // Murni tanpa dummy. Kalau gagal, berhenti loading dan list tetap kosong.
                    _uiState.update { it.copy(isLoadingClasses = false) }
                }
            )
        }
    }

    // 🌟 FUNGSI 1: Menarik data lama dari Cache Gudang Bawah Tanah
    fun loadInitialData(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = repository.getUserDetail(userId)

            result.fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(isLoading = false, initialUser = user.toUiModel()) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    // 🌟 FUNGSI 2: Mengirim data yang sudah diedit ke UseCase (Mendukung Full Edit)
    fun updateUser(
        id: String, name: String, email: String, identifier: String,
        angkatan: Int?, currentSemester: Int?, dosenPa: String?, kodeKelas: String?, ipk: Double?,
        profilePicture: File?
    ) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            val result = updateUserUseCase(
                id, name, email, identifier,
                angkatan, currentSemester, dosenPa, kodeKelas, ipk,
                profilePicture
            )

            result.fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(isLoading = false, successMessage = "Data pengguna berhasil diperbarui!")
                    }
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        state.copy(isLoading = false, errorMessage = error.message)
                    }
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}