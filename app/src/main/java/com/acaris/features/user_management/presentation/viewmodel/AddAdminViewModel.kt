package com.acaris.features.user_management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.user_management.domain.usecase.AddAdminUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class AddAdminState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class AddAdminViewModel @Inject constructor(
    private val addAdminUseCase: AddAdminUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddAdminState())
    val uiState: StateFlow<AddAdminState> = _uiState.asStateFlow()

    fun addAdmin(name: String, email: String, nip: String, password: String, profilePicture: File?) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            val result = addAdminUseCase(name, email, nip, password, profilePicture)

            result.fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(isLoading = false, successMessage = "Admin $name berhasil ditambahkan!")
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