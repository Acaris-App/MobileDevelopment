package com.acaris.features.dashboard.data.remote.datasource

import com.acaris.core.network.model.BaseResponse
import com.acaris.features.dashboard.data.remote.model.DashboardDosenResponse
import com.acaris.features.dashboard.data.remote.model.DashboardMahasiswaResponse
import retrofit2.http.GET

interface DashboardApiService {

    @GET("mahasiswa/dashboard")
    suspend fun getDashboardMahasiswa(): BaseResponse<DashboardMahasiswaResponse>

    @GET("dosen/dashboard")
    suspend fun getDashboardDosen(): BaseResponse<DashboardDosenResponse>
}