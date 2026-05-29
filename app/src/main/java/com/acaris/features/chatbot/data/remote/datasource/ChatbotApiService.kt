package com.acaris.features.chatbot.data.remote.datasource

import com.acaris.core.network.model.BaseResponse
import com.acaris.features.chatbot.data.remote.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatbotApiService {

    @GET("chatbot/session/active")
    suspend fun getActiveSession(): BaseResponse<ChatSessionResponse?>

    @POST("chatbot/message")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): BaseResponse<ChatReplyResponse>

    @POST("chatbot/session/{session_id}/generate-summary")
    suspend fun generateSummary(
        @Path("session_id") sessionId: String
    ): BaseResponse<ChatSummaryResponse>

    @POST("chatbot/session/{session_id}/close")
    suspend fun closeSession(
        @Path("session_id") sessionId: String,
        @Body request: CloseSessionRequest
    ): BaseResponse<Unit>

    @GET("chatbot/history")
    suspend fun getChatHistory(): BaseResponse<List<ChatHistoryItemResponse>>

    @GET("chatbot/history/{session_id}")
    suspend fun getChatHistoryDetail(
        @Path("session_id") sessionId: String
    ): BaseResponse<ChatSessionResponse>
}