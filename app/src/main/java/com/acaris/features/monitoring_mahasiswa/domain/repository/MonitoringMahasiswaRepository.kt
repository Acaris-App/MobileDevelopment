package com.acaris.features.monitoring_mahasiswa.domain.repository

import com.acaris.features.chatbot.domain.model.ChatHistoryDomain
import com.acaris.features.chatbot.domain.model.ChatSessionDomain
import com.acaris.features.monitoring_mahasiswa.domain.model.DetailMahasiswa
import com.acaris.features.monitoring_mahasiswa.domain.model.MahasiswaBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.model.RiwayatBimbingan

interface MonitoringMahasiswaRepository {
    suspend fun getDaftarMahasiswa(): Result<List<MahasiswaBimbingan>>
    suspend fun getDetailMahasiswa(mahasiswaId: String): Result<DetailMahasiswa>
    suspend fun getRiwayatBimbingan(mahasiswaId: String): Result<List<RiwayatBimbingan>>
    suspend fun getMahasiswaChatbotHistory(mahasiswaId: String): Result<List<ChatHistoryDomain>>
    suspend fun getMahasiswaChatbotDetail(mahasiswaId: String, sessionId: String): Result<ChatSessionDomain>
}