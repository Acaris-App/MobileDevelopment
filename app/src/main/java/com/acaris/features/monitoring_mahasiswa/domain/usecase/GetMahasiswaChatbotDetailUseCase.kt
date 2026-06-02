package com.acaris.features.monitoring_mahasiswa.domain.usecase

import com.acaris.features.chatbot.domain.model.ChatSessionDomain
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMahasiswaChatbotDetailUseCase @Inject constructor(
    private val repository: MonitoringMahasiswaRepository
) {
    operator fun invoke(mahasiswaId: String, sessionId: String): Flow<ChatSessionDomain> {
        return repository.getMahasiswaChatbotDetail(mahasiswaId, sessionId)
    }
}