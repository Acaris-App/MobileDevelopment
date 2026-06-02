package com.acaris.features.monitoring_mahasiswa.data.repository

import com.acaris.features.monitoring_mahasiswa.data.mapper.toDomain
import com.acaris.features.monitoring_mahasiswa.data.remote.datasource.MonitoringMahasiswaApiService
import com.acaris.features.monitoring_mahasiswa.domain.model.DetailMahasiswa
import com.acaris.features.monitoring_mahasiswa.domain.model.MahasiswaBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.model.RiwayatBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import com.acaris.features.chatbot.domain.model.ChatHistoryDomain
import com.acaris.features.chatbot.data.mapper.toDomain
import com.acaris.features.chatbot.domain.model.ChatSessionDomain
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
        val data = response.data ?: emptyList()
        emit(data.map { it.toDomain() })
    }.flowOn(Dispatchers.IO)

    override fun getMahasiswaChatbotHistory(mahasiswaId: String): Flow<List<ChatHistoryDomain>> = flow {
        val response = apiService.getMahasiswaChatbotHistory(mahasiswaId)
        val data = response.data ?: emptyList()
        emit(data.map { it.toDomain() })
    }.flowOn(Dispatchers.IO)

    override fun getMahasiswaChatbotDetail(mahasiswaId: String, sessionId: String): Flow<ChatSessionDomain> = flow {
        val response = apiService.getMahasiswaChatbotDetail(mahasiswaId, sessionId)
        val data = response.data ?: throw Exception("Data Detail Chatbot Tidak Ditemukan")
        emit(data.toDomain())
    }.flowOn(Dispatchers.IO)
}