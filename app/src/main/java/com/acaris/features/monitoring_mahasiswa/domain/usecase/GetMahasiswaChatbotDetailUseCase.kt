package com.acaris.features.monitoring_mahasiswa.domain.usecase

import com.acaris.features.chatbot.domain.model.ChatSessionDomain
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import javax.inject.Inject

class GetMahasiswaChatbotDetailUseCase @Inject constructor(
    private val repository: MonitoringMahasiswaRepository
) {
    suspend operator fun invoke(mahasiswaId: String, sessionId: String): Result<ChatSessionDomain> {
        return repository.getMahasiswaChatbotDetail(mahasiswaId, sessionId)
    }
}