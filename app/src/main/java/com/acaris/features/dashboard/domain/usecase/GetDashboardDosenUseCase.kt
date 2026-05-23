package com.acaris.features.dashboard.domain.usecase

import com.acaris.features.dashboard.domain.model.DashboardDosen
import com.acaris.features.dashboard.domain.repository.DashboardRepository
import javax.inject.Inject

class GetDashboardDosenUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(): Result<DashboardDosen> {
        return repository.getDashboardDosen()
    }
}