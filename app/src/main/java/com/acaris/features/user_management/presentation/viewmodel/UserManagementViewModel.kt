package com.acaris.features.user_management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.user_management.domain.usecase.UserManagementUseCases
import com.acaris.features.user_management.presentation.mapper.toUiModel
import com.acaris.features.user_management.presentation.model.UserManagementState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val useCases: UserManagementUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserManagementState())
    val uiState: StateFlow<UserManagementState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val currentState = _uiState.value

        viewModelScope.launch {
            val result = useCases.getUsers(
                role = currentState.currentRole,
                search = currentState.currentSearch.takeIf { it.isNotBlank() },
                sortBy = currentState.currentSortBy
            )

            result.fold(
                onSuccess = { usersDomain ->
                    val uiModels = usersDomain.map { it.toUiModel() }
                    _uiState.update { it.copy(isLoading = false, users = uiModels) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun setFilterAndLoad(role: String) {
        _uiState.update { it.copy(currentRole = role) }
        loadUsers()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(currentSearch = query) }
        // Note: Untuk search, Kapten mungkin butuh mekanisme 'debounce' (jeda ngetik)
        // di UI agar tidak nge-hit API setiap 1 huruf diketik.
        // Panggil loadUsers() setelah user selesai ngetik.
    }

    fun setSortByAndLoad(sortBy: String) {
        _uiState.update { it.copy(currentSortBy = sortBy) }
        loadUsers()
    }

    fun changeUserStatus(id: String, newActiveStatus: Boolean) {
        _uiState.update { it.copy(isActionLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = useCases.changeUserStatus(id, newActiveStatus)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isActionLoading = false, successMessage = "Status pengguna berhasil diperbarui.")
                    }
                    loadUsers() // Refresh data
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isActionLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun deleteUser(id: String, currentStatus: String) {
        _uiState.update { it.copy(isActionLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = useCases.deleteUser(id, currentStatus)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isActionLoading = false, successMessage = "Pengguna berhasil dihapus permanen.")
                    }
                    loadUsers()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isActionLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}