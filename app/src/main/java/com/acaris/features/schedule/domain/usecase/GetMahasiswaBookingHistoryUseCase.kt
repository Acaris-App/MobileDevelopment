package com.acaris.features.schedule.domain.usecase

import com.acaris.features.schedule.domain.model.Schedule
import com.acaris.features.schedule.domain.repository.ScheduleRepository
import javax.inject.Inject

class GetMahasiswaBookingHistoryUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(): Result<List<Schedule>> {
        return repository.getMahasiswaBookingHistory()
    }
}