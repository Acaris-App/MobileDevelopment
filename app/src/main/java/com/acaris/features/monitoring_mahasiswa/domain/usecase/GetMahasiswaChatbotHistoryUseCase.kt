package com.acaris.features.monitoring_mahasiswa.domain.usecase

import com.acaris.features.chatbot.domain.model.ChatHistoryDomain
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMahasiswaChatbotHistoryUseCase @Inject constructor(
    private val repository: MonitoringMahasiswaRepository
) {
    operator fun invoke(mahasiswaId: String): Flow<List<ChatHistoryDomain>> {
        return repository.getMahasiswaChatbotHistory(mahasiswaId)
    }
}