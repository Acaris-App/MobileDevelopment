package com.acaris.features.dashboard.domain.usecase

import com.acaris.features.dashboard.domain.model.DashboardMahasiswa
import com.acaris.features.dashboard.domain.repository.DashboardRepository
import javax.inject.Inject

class GetDashboardMahasiswaUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(): Result<DashboardMahasiswa> {
        // Panggil kontrak dari repository
        return repository.getDashboardMahasiswa()
    }
}