package com.acaris.features.chatbot.data.repository

import com.acaris.core.network.parseApiError
import com.acaris.features.chatbot.data.mapper.toDomain
import com.acaris.features.chatbot.data.remote.datasource.ChatbotApiService
import com.acaris.features.chatbot.data.remote.model.CloseSessionRequest
import com.acaris.features.chatbot.data.remote.model.SendMessageRequest
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
            // 🌟 FIX: Bungkus pesan error asli dari API ke dalam Exception baru
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
            // 🌟 FIX: Gunakan parseApiError()
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
            // 🌟 FIX: Gunakan parseApiError() agar HTTP 502 Bad Gateway berubah jadi pesan JSON
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
            // 🌟 FIX: Gunakan parseApiError()
            Result.failure(Exception(e.parseApiError()))
        }
    }
}