package com.acaris.features.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.schedule.domain.usecase.CreateScheduleUseCase
import com.acaris.features.schedule.domain.usecase.DeleteScheduleUseCase
import com.acaris.features.schedule.domain.usecase.GetDailySchedulesUseCase
import com.acaris.features.schedule.domain.usecase.GetMonthlySchedulesUseCase
import com.acaris.features.schedule.domain.usecase.UpdateScheduleUseCase
import com.acaris.features.schedule.presentation.mapper.toPresentation
import com.acaris.features.schedule.presentation.model.ScheduleStatus
import com.acaris.features.schedule.presentation.model.ScheduleUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val createScheduleUseCase: CreateScheduleUseCase,
    private val getMonthlySchedulesUseCase: GetMonthlySchedulesUseCase,
    private val getDailySchedulesUseCase: GetDailySchedulesUseCase,
    private val deleteScheduleUseCase: DeleteScheduleUseCase,
    private val updateScheduleUseCase: UpdateScheduleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    fun createSchedule(date: String, startTime: String, endTime: String, quota: Int, keterangan: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }
        viewModelScope.launch {
            val result = createScheduleUseCase(date, startTime, endTime, quota, keterangan)
            result.fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, isSuccess = true) } },
                onFailure = { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
            )
        }
    }

    fun updateSchedule(id: String, date: String, startTime: String, endTime: String, quota: Int, keterangan: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }
        viewModelScope.launch {
            val result = updateScheduleUseCase(id, date, startTime, endTime, quota, keterangan)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun fetchMonthlySchedules(year: Int, month: Int) {
        viewModelScope.launch {
            val result = getMonthlySchedulesUseCase(year, month)
            result.onSuccess { schedules ->
                val statusMap = mutableMapOf<LocalDate, ScheduleStatus>()

                schedules.forEach { schedule ->
                    try {
                        val date = LocalDate.parse(schedule.date) // 🌟 Data sudah bersih dari Data Layer

                        val isPast = schedule.isExpired()
                        val isAvailable = schedule.remainingQuota > 0
                        val currentMapStatus = statusMap[date]

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

                _uiState.update { it.copy(monthlyScheduleMap = statusMap.toMap()) }
            }.onFailure { }
        }
    }

    fun fetchDailySchedule(date: String) {
        _uiState.update { it.copy(isLoading = true, dailySchedules = emptyList(), errorMessage = null) }
        viewModelScope.launch {
            val result = getDailySchedulesUseCase(date)
            result.onSuccess { schedules ->
                val details = schedules.map { it.toPresentation() }
                _uiState.update { it.copy(isLoading = false, dailySchedules = details) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun deleteSchedule(id: String, date: String, year: Int, month: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }
        viewModelScope.launch {
            val result = deleteScheduleUseCase(id)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true, dailySchedules = emptyList()) }
                fetchMonthlySchedules(year, month)
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun resetState() {
        _uiState.update { it.copy(isLoading = false, isSuccess = false, errorMessage = null, createdSchedule = null) }
    }
}