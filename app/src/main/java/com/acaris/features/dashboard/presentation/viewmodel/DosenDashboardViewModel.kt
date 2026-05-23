package com.acaris.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.dashboard.domain.usecase.GetDashboardDosenUseCase
import com.acaris.features.dashboard.presentation.mapper.toPresentation
import com.acaris.features.dashboard.presentation.model.DashboardDosenState
import com.acaris.features.dashboard.presentation.model.DashboardDosenUiModel
// 🌟 IMPOR SAKTI: Meminjam UseCase dari fitur Schedule khusus Dosen
import com.acaris.features.schedule.domain.usecase.GetMonthlySchedulesUseCase
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
class DosenDashboardViewModel @Inject constructor(
    private val getDashboardDosenUseCase: GetDashboardDosenUseCase,
    // 🌟 SUNTIKAN: Mengambil UseCase Schedule
    private val getMonthlySchedulesUseCase: GetMonthlySchedulesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardDosenState(dashboardData = DashboardDosenUiModel.empty())
    )
    val uiState: StateFlow<DashboardDosenState> = _uiState.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    fun loadDashboard() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = getDashboardDosenUseCase()

            result.fold(
                onSuccess = { domainData ->
                    val uiData = domainData.toPresentation()
                    _uiState.update {
                        it.copy(isLoading = false, dashboardData = uiData)
                    }
                    // 🌟 Tarik data kalender full setelah dashboard utama dosen termuat
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

    // 🌟 LOGIKA PEWARNAAN DIANUT DARI SCHEDULE VIEWMODEL DOSEN KAPTEN
    private fun fetchFullMonthlySchedules(year: Int, month: Int) {
        viewModelScope.launch {
            val result = getMonthlySchedulesUseCase(year, month)
            result.onSuccess { schedules ->
                val statusMap = mutableMapOf<LocalDate, ScheduleStatus>()

                schedules.forEach { schedule ->
                    try {
                        val date = LocalDate.parse(schedule.date)

                        val isPast = schedule.isExpired()
                        val isAvailable = schedule.remainingQuota > 0
                        val currentMapStatus = statusMap[date]

                        // Prioritas Dosen: Warna MERAH (Full) adalah prioritas agar jadwal mhs tdk tertimpa
                        if (currentMapStatus == null || currentMapStatus == ScheduleStatus.FULL || currentMapStatus == ScheduleStatus.SELESAI) {
                            statusMap[date] = when {
                                isPast -> ScheduleStatus.SELESAI
                                isAvailable -> ScheduleStatus.AVAILABLE
                                else -> ScheduleStatus.FULL
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // 🌟 Timpa data kalender bawaan UI Dashboard Dosen dengan data kalender full
                _uiState.update { currentState ->
                    val currentData = currentState.dashboardData ?: DashboardDosenUiModel.empty()
                    currentState.copy(
                        dashboardData = currentData.copy(kalenderBimbinganMap = statusMap.toMap())
                    )
                }
            }.onFailure {
                // Abaikan jika error, biarkan UI memakai kalender bawaan dashboard dosen
            }
        }
    }

    fun onMonthChanged(newMonth: YearMonth) {
        _currentMonth.value = newMonth
        fetchFullMonthlySchedules(newMonth.year, newMonth.monthValue)
    }
}