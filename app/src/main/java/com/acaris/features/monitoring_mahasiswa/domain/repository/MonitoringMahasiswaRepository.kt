package com.acaris.features.monitoring_mahasiswa.domain.repository

import com.acaris.features.monitoring_mahasiswa.domain.model.DetailMahasiswa
import com.acaris.features.monitoring_mahasiswa.domain.model.MahasiswaBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.model.RiwayatBimbingan
import com.acaris.features.chatbot.domain.model.ChatHistoryDomain // 🌟 IMPORT DARI FITUR CHATBOT
import com.acaris.features.chatbot.domain.model.ChatSessionDomain
import kotlinx.coroutines.flow.Flow

interface MonitoringMahasiswaRepository {
    fun getDaftarMahasiswa(): Flow<List<MahasiswaBimbingan>>

    fun getDetailMahasiswa(mahasiswaId: String): Flow<DetailMahasiswa>

    fun getRiwayatBimbingan(mahasiswaId: String): Flow<List<RiwayatBimbingan>>

    fun getMahasiswaChatbotHistory(mahasiswaId: String): Flow<List<ChatHistoryDomain>>

    fun getMahasiswaChatbotDetail(mahasiswaId: String, sessionId: String): Flow<ChatSessionDomain>
}