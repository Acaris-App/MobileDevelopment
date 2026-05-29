package com.acaris.features.chatbot.data.repository

import com.acaris.core.network.parseApiError
import com.acaris.features.chatbot.data.mapper.toDomain
import com.acaris.features.chatbot.data.remote.datasource.ChatbotApiService
import com.acaris.features.chatbot.data.remote.model.CloseSessionRequest
import com.acaris.features.chatbot.data.remote.model.SendMessageRequest
import com.acaris.features.chatbot.domain.model.ChatHistoryDomain
import com.acaris.features.chatbot.domain.model.ChatReplyDomain
import com.acaris.features.chatbot.domain.model.ChatSessionDomain
import com.acaris.features.chatbot.domain.model.ChatSummaryDomain
import com.acaris.features.chatbot.domain.repository.ChatbotRepository
import javax.inject.Inject

class ChatbotRepositoryImpl @Inject constructor(
    private val apiService: ChatbotApiService
) : ChatbotRepository {

    override suspend fun getActiveSession(): Result<ChatSessionDomain?> {
        return try {
            val response = apiService.getActiveSession()
            if (response.status == "success" || response.status == "200") {
                val domainData = response.data?.toDomain()
                Result.success(domainData)
            } else {
                Result.failure(Exception(response.message ?: "Gagal memuat sesi aktif"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun sendMessage(sessionId: String?, message: String): Result<ChatReplyDomain> {
        return try {
            val request = SendMessageRequest(sessionId = sessionId, message = message)
            val response = apiService.sendMessage(request)

            if (response.status == "success" || response.status == "200") {
                val domainData = response.data?.toDomain() ?: throw Exception("Data balasan kosong dari server")
                Result.success(domainData)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengirim pesan"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun generateSummary(sessionId: String): Result<ChatSummaryDomain> {
        return try {
            val response = apiService.generateSummary(sessionId)
            if (response.status == "success" || response.status == "200") {
                val domainData = response.data?.toDomain() ?: throw Exception("Data rangkuman kosong dari server")
                Result.success(domainData)
            } else {
                Result.failure(Exception(response.message ?: "Gagal merangkum obrolan"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun closeSession(sessionId: String, finalSummary: String): Result<Unit> {
        return try {
            val request = CloseSessionRequest(finalSummary = finalSummary)
            val response = apiService.closeSession(sessionId, request)

            if (response.status == "success" || response.status == "200") {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengakhiri sesi bimbingan"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getChatHistory(): Result<List<ChatHistoryDomain>> {
        return try {
            val response = apiService.getChatHistory()

            if (response.status == "success" || response.status == "200") {
                val domainData = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(domainData)
            } else {
                Result.failure(Exception(response.message ?: "Gagal memuat riwayat obrolan"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }

    override suspend fun getChatHistoryDetail(sessionId: String): Result<ChatSessionDomain> {
        return try {
            val response = apiService.getChatHistoryDetail(sessionId)
            if (response.status == "success" || response.status == "200") {
                val domainData = response.data?.toDomain() ?: throw Exception("Data detail kosong")
                Result.success(domainData)
            } else {
                Result.failure(Exception(response.message ?: "Gagal memuat detail riwayat"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.parseApiError()))
        }
    }
}