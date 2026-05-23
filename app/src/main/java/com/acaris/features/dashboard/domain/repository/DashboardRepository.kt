package com.acaris.features.dashboard.domain.repository

import com.acaris.features.dashboard.domain.model.DashboardDosen
import com.acaris.features.dashboard.domain.model.DashboardMahasiswa

interface DashboardRepository {
    suspend fun getDashboardMahasiswa(): Result<DashboardMahasiswa>
    suspend fun getDashboardDosen(): Result<DashboardDosen>
}