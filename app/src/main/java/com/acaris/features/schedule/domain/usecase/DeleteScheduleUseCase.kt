package com.acaris.features.schedule.domain.usecase

import com.acaris.features.schedule.domain.repository.ScheduleRepository
import javax.inject.Inject

class DeleteScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(id: String): Result<Boolean> {
        return repository.deleteSchedule(id)
    }
}