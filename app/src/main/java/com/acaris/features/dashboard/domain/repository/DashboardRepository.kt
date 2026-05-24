package com.acaris.features.dashboard.domain.repository

import com.acaris.features.dashboard.domain.model.*

interface DashboardRepository {
    suspend fun getDashboardMahasiswa(): Result<DashboardMahasiswa>
    suspend fun getDashboardDosen(): Result<DashboardDosen>
    suspend fun getDashboardAdmin(): Result<DashboardAdmin>
}