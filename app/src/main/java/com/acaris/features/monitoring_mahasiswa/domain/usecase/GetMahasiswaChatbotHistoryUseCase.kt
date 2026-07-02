package com.acaris.features.monitoring_mahasiswa.domain.usecase

import com.acaris.features.chatbot.domain.model.ChatHistoryDomain
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import javax.inject.Inject

class GetMahasiswaChatbotHistoryUseCase @Inject constructor(
    private val repository: MonitoringMahasiswaRepository
) {
    suspend operator fun invoke(mahasiswaId: String): Result<List<ChatHistoryDomain>> {
        return repository.getMahasiswaChatbotHistory(mahasiswaId)
    }
}