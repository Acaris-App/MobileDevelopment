package com.acaris.features.schedule.domain.usecase

import com.acaris.features.schedule.domain.model.Schedule
import com.acaris.features.schedule.domain.repository.ScheduleRepository
import javax.inject.Inject

class GetMahasiswaDailySchedulesUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(date: String): Result<List<Schedule>> {
        return repository.getMahasiswaDailySchedules(date)
    }
}