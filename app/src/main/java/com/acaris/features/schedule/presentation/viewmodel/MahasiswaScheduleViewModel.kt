package com.acaris.features.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.schedule.domain.usecase.BookScheduleUseCase
import com.acaris.features.schedule.domain.usecase.GetMahasiswaBookingHistoryUseCase
import com.acaris.features.schedule.domain.usecase.GetMahasiswaDailySchedulesUseCase
import com.acaris.features.schedule.domain.usecase.GetMahasiswaMonthlySchedulesUseCase
import com.acaris.features.schedule.presentation.mapper.toMahasiswaPresentation
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
class MahasiswaScheduleViewModel @Inject constructor(
    private val getMonthlyUseCase: GetMahasiswaMonthlySchedulesUseCase,
    private val getDailyUseCase: GetMahasiswaDailySchedulesUseCase,
    private val bookScheduleUseCase: BookScheduleUseCase,
    private val getHistoryUseCase: GetMahasiswaBookingHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    fun fetchMonthlySchedules(year: Int, month: Int) {
        viewModelScope.launch {
            val result = getMonthlyUseCase(year, month)
            result.onSuccess { schedules ->
                val statusMap = mutableMapOf<LocalDate, ScheduleStatus>()

                schedules.forEach { schedule ->
                    try {
                        val date = LocalDate.parse(schedule.date) // 🌟 Langsung parse, sudah bersih!

                        val isPast = schedule.isExpired()
                        val isBookedByMe = schedule.bookingId != null
                        val isAvailable = schedule.remainingQuota > 0
                        val currentMapStatus = statusMap[date]

                        if (currentMapStatus != ScheduleStatus.BOOKED_BY_ME) {
                            statusMap[date] = when {
                                isPast -> ScheduleStatus.SELESAI // 🌟 Kalender bulan lalu otomatis Kuning
                                isBookedByMe -> ScheduleStatus.BOOKED_BY_ME
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
            val result = getDailyUseCase(date)
            result.onSuccess { schedules ->
                val details = schedules.map { it.toMahasiswaPresentation() }
                _uiState.update { it.copy(isLoading = false, dailySchedules = details) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun bookSchedule(scheduleId: String, agenda: String, selectedDate: String, year: Int, month: Int) {
        if (agenda.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Agenda bimbingan wajib diisi.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }
        viewModelScope.launch {
            val result = bookScheduleUseCase(scheduleId, agenda)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                fetchDailySchedule(selectedDate)
                fetchMonthlySchedules(year, month)
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun fetchBookingHistory() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = getHistoryUseCase()
            result.onSuccess { schedules ->
                val historyList = schedules.map { it.toMahasiswaPresentation() }
                _uiState.update { it.copy(isLoading = false, bookingHistory = historyList) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun resetState() {
        _uiState.update { it.copy(isLoading = false, isSuccess = false, errorMessage = null) }
    }
}