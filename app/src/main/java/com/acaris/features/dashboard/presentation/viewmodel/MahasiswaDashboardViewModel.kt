package com.acaris.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.dashboard.domain.usecase.GetDashboardMahasiswaUseCase
import com.acaris.features.dashboard.presentation.mapper.toPresentation
import com.acaris.features.dashboard.presentation.model.DashboardMahasiswaState
import com.acaris.features.dashboard.presentation.model.DashboardMahasiswaUiModel
import com.acaris.features.schedule.domain.usecase.GetMahasiswaMonthlySchedulesUseCase
import com.acaris.features.schedule.presentation.model.ScheduleStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class MahasiswaDashboardViewModel @Inject constructor(
    private val getDashboardMahasiswaUseCase: GetDashboardMahasiswaUseCase,
    private val getMonthlySchedulesUseCase: GetMahasiswaMonthlySchedulesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardMahasiswaState(dashboardData = DashboardMahasiswaUiModel.empty())
    )
    val uiState: StateFlow<DashboardMahasiswaState> = _uiState.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    fun loadDashboard() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = getDashboardMahasiswaUseCase()

            result.fold(
                onSuccess = { domainData ->
                    val uiData = domainData.toPresentation()
                    _uiState.update {
                        it.copy(isLoading = false, dashboardData = uiData)
                    }

                    fetchFullMonthlySchedules(_currentMonth.value.year, _currentMonth.value.monthValue)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
            )
        }
    }

    private fun fetchFullMonthlySchedules(year: Int, month: Int) {
        viewModelScope.launch {
            val result = getMonthlySchedulesUseCase(year, month)
            result.onSuccess { schedules ->
                val statusMap = mutableMapOf<LocalDate, ScheduleStatus>()

                schedules.forEach { schedule ->
                    try {
                        val date = LocalDate.parse(schedule.date)

                        val isPast = schedule.isExpired()
                        val isBookedByMe = schedule.bookingId != null
                        val isAvailable = schedule.remainingQuota > 0
                        val currentMapStatus = statusMap[date]

                        // Sistem kasta: Jangan timpa kalau sudah BOOKED_BY_ME
                        if (currentMapStatus != ScheduleStatus.BOOKED_BY_ME) {
                            statusMap[date] = when {
                                isPast -> ScheduleStatus.SELESAI
                                isBookedByMe -> ScheduleStatus.BOOKED_BY_ME
                                isAvailable -> ScheduleStatus.AVAILABLE
                                else -> ScheduleStatus.FULL
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                _uiState.update { currentState ->
                    val currentData = currentState.dashboardData ?: DashboardMahasiswaUiModel.empty()
                    currentState.copy(
                        dashboardData = currentData.copy(kalenderBimbinganMap = statusMap.toMap())
                    )
                }
            }.onFailure {
                // Jika API Schedule gagal, abaikan saja, biarkan menggunakan kalender bawaan Dashboard
            }
        }
    }

    fun onMonthChanged(newMonth: YearMonth) {
        _currentMonth.value = newMonth
        fetchFullMonthlySchedules(newMonth.year, newMonth.monthValue)
    }
}