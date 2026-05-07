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

    fun loadUsers(isRefresh: Boolean = true) {
        val currentState = _uiState.value

        if (isRefresh) {
            _uiState.update { it.copy(isLoading = true, isAppending = false, currentPage = 1, isLastPage = false, errorMessage = null) }
        } else {
            // Jika sedang muat halaman bawah atau sudah halaman terakhir, batalkan!
            if (currentState.isLastPage || currentState.isAppending) return
            _uiState.update { it.copy(isAppending = true, errorMessage = null) }
        }

        val targetPage = if (isRefresh) 1 else currentState.currentPage + 1

        viewModelScope.launch {
            val result = useCases.getUsers(
                role = currentState.currentRole,
                search = currentState.currentSearch.takeIf { it.isNotBlank() },
                sortBy = currentState.currentSortBy,
                page = targetPage // 🌟 TEMBAK HALAMAN
            )

            result.fold(
                onSuccess = { usersDomain ->
                    val uiModels = usersDomain.map { it.toUiModel() }

                    _uiState.update { state ->
                        // Jika refresh, timpa. Jika append, gabungkan list lama & baru!
                        val newList = if (isRefresh) uiModels else state.users + uiModels

                        state.copy(
                            isLoading = false,
                            isAppending = false,
                            users = newList,
                            currentPage = targetPage,
                            // 🌟 TRIK RAHASIA: Jika data yang datang kurang dari 20, berarti ini halaman terakhir!
                            isLastPage = uiModels.isEmpty() || uiModels.size < 20
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, isAppending = false, errorMessage = error.message) }
                }
            )
        }
    }

    // 🌟 PANGGIL INI SAAT SCROLL MENTOK BAWAH
    fun loadNextPage() {
        loadUsers(isRefresh = false)
    }

    fun setFilterAndLoad(role: String) {
        _uiState.update { it.copy(currentRole = role) }
        loadUsers(isRefresh = true) // Wajib true agar ulang dari page 1
    }

    fun setSortByAndLoad(sortBy: String) {
        _uiState.update { it.copy(currentSortBy = sortBy) }
        loadUsers(isRefresh = true)
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(currentSearch = query) }
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
                    loadUsers()
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