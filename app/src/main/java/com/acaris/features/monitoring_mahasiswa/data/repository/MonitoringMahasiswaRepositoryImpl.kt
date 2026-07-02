package com.acaris.features.monitoring_mahasiswa.data.repository

import com.acaris.core.network.parseApiError // 🌟 Import ini agar error rapi seperti di Schedule
import com.acaris.features.monitoring_mahasiswa.data.mapper.toDomain
import com.acaris.features.monitoring_mahasiswa.data.remote.datasource.MonitoringMahasiswaApiService
import com.acaris.features.monitoring_mahasiswa.domain.model.DetailMahasiswa
import com.acaris.features.monitoring_mahasiswa.domain.model.MahasiswaBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.model.RiwayatBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import com.acaris.features.chatbot.domain.model.ChatHistoryDomain
import com.acaris.features.chatbot.data.mapper.toDomain
import com.acaris.features.chatbot.domain.model.ChatSessionDomain
import javax.inject.Inject

class MonitoringMahasiswaRepositoryImpl @Inject constructor(
    private val apiService: MonitoringMahasiswaApiService
) : MonitoringMahasiswaRepository {

    override suspend fun getDaftarMahasiswa(): Result<List<MahasiswaBimbingan>> {
        return try {
            val response = apiService.getDaftarMahasiswa()
            if (response.status == "success" || response.status == "200") {
                val data = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengambil daftar mahasiswa"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getDetailMahasiswa(mahasiswaId: String): Result<DetailMahasiswa> {
        return try {
            val response = apiService.getDetailMahasiswa(mahasiswaId)
            if (response.status == "success" || response.status == "200") {
                val data = response.data?.toDomain() ?: throw Exception("Data Mahasiswa Tidak Ditemukan")
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengambil detail mahasiswa"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getRiwayatBimbingan(mahasiswaId: String): Result<List<RiwayatBimbingan>> {
        return try {
            val response = apiService.getRiwayatBimbingan(mahasiswaId)
            if (response.status == "success" || response.status == "200") {
                val data = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengambil riwayat bimbingan"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getMahasiswaChatbotHistory(mahasiswaId: String): Result<List<ChatHistoryDomain>> {
        return try {
            val response = apiService.getMahasiswaChatbotHistory(mahasiswaId)
            if (response.status == "success" || response.status == "200") {
                val data = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengambil riwayat chatbot"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getMahasiswaChatbotDetail(mahasiswaId: String, sessionId: String): Result<ChatSessionDomain> {
        return try {
            val response = apiService.getMahasiswaChatbotDetail(mahasiswaId, sessionId)
            if (response.status == "success" || response.status == "200") {
                val data = response.data?.toDomain() ?: throw Exception("Data Detail Chatbot Tidak Ditemukan")
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengambil detail chatbot"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }
}