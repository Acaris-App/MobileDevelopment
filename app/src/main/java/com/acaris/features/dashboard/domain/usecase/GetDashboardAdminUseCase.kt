package com.acaris.features.dashboard.domain.usecase

import com.acaris.features.dashboard.domain.model.DashboardAdmin
import com.acaris.features.dashboard.domain.repository.DashboardRepository
import javax.inject.Inject

class GetDashboardAdminUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(): Result<DashboardAdmin> {
        return repository.getDashboardAdmin()
    }
}