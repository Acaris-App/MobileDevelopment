package com.acaris.features.user_management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.user_management.domain.repository.UserManagementRepository
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
    private val useCases: UserManagementUseCases,
    private val repository: UserManagementRepository // 🌟 KITA SUNTIK INI untuk mendengarkan perubahan langsung dari sumbernya
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserManagementState())
    val uiState: StateFlow<UserManagementState> = _uiState.asStateFlow()

    init {
        observeUsers() // 🌟 Pasang telinga (Observer) sebelum memuat data
        loadUsers()
    }

    // 🌟 FUNGSI BARU: Mendengarkan aliran data (Flow) dari Repository secara Real-Time!
    private fun observeUsers() {
        viewModelScope.launch {
            repository.usersFlow.collect { updatedList ->
                _uiState.update { state ->
                    // Setiap kali ada Edit/Hapus/Tambah, UI otomatis me-render ulang!
                    state.copy(users = updatedList.map { it.toUiModel() })
                }
            }
        }
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
                page = targetPage
            )

            result.fold(
                onSuccess = { usersDomain ->
                    // 🌟 KITA TIDAK PERLU LAGI MENGGABUNGKAN LIST SECARA MANUAL DI SINI.
                    // Repository sudah melakukannya dan menembakkannya lewat `observeUsers()`!
                    // Di sini kita cukup atur state loading & paginasi saja.
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isAppending = false,
                            currentPage = targetPage,
                            isLastPage = usersDomain.isEmpty() || usersDomain.size < 20
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, isAppending = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun loadNextPage() {
        loadUsers(isRefresh = false)
    }

    fun setFilterAndLoad(role: String) {
        _uiState.update { it.copy(currentRole = role) }
        loadUsers(isRefresh = true)
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
                    // 🌟 SUPER EFISIEN: Dulu kita panggil loadUsers() di sini yang boros kuota API.
                    // Sekarang dihapus! Karena aliran usersFlow akan otomatis memperbarui tampilan di UI.
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
                    // 🌟 SUPER EFISIEN: Dihapus juga dari sini!
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