package com.acaris.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.dashboard.domain.usecase.GetDashboardAdminUseCase
import com.acaris.features.dashboard.presentation.mapper.toPresentation
import com.acaris.features.dashboard.presentation.model.DashboardAdminState // 🌟 KEMBALI KE STATE
import com.acaris.features.dashboard.presentation.model.DashboardAdminUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val getDashboardAdminUseCase: GetDashboardAdminUseCase
) : ViewModel() {

    // 🌟 KEMBALI KE STATE
    private val _uiState = MutableStateFlow(
        DashboardAdminState(dashboardData = DashboardAdminUiModel.empty())
    )
    val uiState: StateFlow<DashboardAdminState> = _uiState.asStateFlow()

    fun loadDashboard() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = getDashboardAdminUseCase()

            result.fold(
                onSuccess = { domainData ->
                    val uiData = domainData.toPresentation()
                    _uiState.update {
                        it.copy(isLoading = false, dashboardData = uiData)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
            )
        }
    }
}