package com.acaris.features.schedule.domain.usecase

import com.acaris.features.schedule.domain.repository.ScheduleRepository
import javax.inject.Inject

class BookScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(scheduleId: String, agenda: String): Result<Boolean> {
        return repository.bookSchedule(scheduleId, agenda)
    }
}