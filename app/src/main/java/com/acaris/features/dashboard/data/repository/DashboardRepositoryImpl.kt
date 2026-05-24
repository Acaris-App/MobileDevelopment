package com.acaris.features.dashboard.data.repository

import com.acaris.core.network.parseApiError
import com.acaris.features.dashboard.data.mapper.toDomain
import com.acaris.features.dashboard.data.remote.datasource.DashboardApiService
import com.acaris.features.dashboard.domain.model.*
import com.acaris.features.dashboard.domain.repository.DashboardRepository
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val apiService: DashboardApiService
) : DashboardRepository {

    override suspend fun getDashboardMahasiswa(): Result<DashboardMahasiswa> {
        return try {
            val response = apiService.getDashboardMahasiswa()
            if (response.status == "success" || response.status == "200") {
                val data = response.data?.toDomain() ?: throw Exception("Data dashboard mahasiswa kosong dari server")
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Terjadi kesalahan dari server"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getDashboardDosen(): Result<DashboardDosen> {
        return try {
            val response = apiService.getDashboardDosen()

            if (response.status == "success" || response.status == "200") {
                val data = response.data?.toDomain() ?: throw Exception("Data dashboard dosen kosong dari server")
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Terjadi kesalahan dari server"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getDashboardAdmin(): Result<DashboardAdmin> {
        return try {
            val response = apiService.getDashboardAdmin()

            if (response.status == "success" || response.status == "200") {
                val data = response.data?.toDomain() ?: throw Exception("Data dashboard admin kosong dari server")
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Terjadi kesalahan dari server"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }
}