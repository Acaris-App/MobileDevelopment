package com.acaris.features.monitoring_mahasiswa.data.repository

import com.acaris.features.monitoring_mahasiswa.data.mapper.toDomain
import com.acaris.features.monitoring_mahasiswa.data.remote.datasource.MonitoringMahasiswaApiService
import com.acaris.features.monitoring_mahasiswa.domain.model.DetailMahasiswa
import com.acaris.features.monitoring_mahasiswa.domain.model.MahasiswaBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.model.RiwayatBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MonitoringMahasiswaRepositoryImpl @Inject constructor(
    private val apiService: MonitoringMahasiswaApiService
) : MonitoringMahasiswaRepository {

    override fun getDaftarMahasiswa(): Flow<List<MahasiswaBimbingan>> = flow {
        val response = apiService.getDaftarMahasiswa()
        val data = response.data ?: emptyList()
        emit(data.map { it.toDomain() })
    }.flowOn(Dispatchers.IO)

    override fun getDetailMahasiswa(mahasiswaId: String): Flow<DetailMahasiswa> = flow {
        val response = apiService.getDetailMahasiswa(mahasiswaId)
        val data = response.data ?: throw Exception("Data Mahasiswa Tidak Ditemukan")
        emit(data.toDomain())
    }.flowOn(Dispatchers.IO)

    override fun getRiwayatBimbingan(mahasiswaId: String): Flow<List<RiwayatBimbingan>> = flow {
        val response = apiService.getRiwayatBimbingan(mahasiswaId)
        val data = response.data ?: emptyList() // Buka bungkus BaseResponse
        emit(data.map { it.toDomain() })
    }.flowOn(Dispatchers.IO)
}