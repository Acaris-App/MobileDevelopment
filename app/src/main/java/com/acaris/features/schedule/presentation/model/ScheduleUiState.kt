package com.acaris.features.schedule.presentation.model

import java.time.LocalDate

enum class ScheduleStatus {
    AVAILABLE, FULL, BOOKED_BY_ME, SELESAI, NONE
}

data class ScheduleUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val createdSchedule: ScheduleUiModel? = null,

    val monthlyScheduleMap: Map<LocalDate, ScheduleStatus> = emptyMap(),
    val dailySchedules: List<ScheduleUiModel> = emptyList(),
    val bookingHistory: List<ScheduleUiModel> = emptyList()
)