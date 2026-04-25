package com.acaris.features.schedule.domain.usecase

import com.acaris.features.schedule.domain.model.Schedule
import com.acaris.features.schedule.domain.repository.ScheduleRepository
import javax.inject.Inject

class GetMahasiswaMonthlySchedulesUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Result<List<Schedule>> {
        return repository.getMahasiswaMonthlySchedules(year, month)
    }
}