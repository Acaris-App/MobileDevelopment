package com.acaris.features.chatbot.data.remote.model

import com.google.gson.annotations.SerializedName

// RESPONSE MODELS (Dari Server ke Aplikasi)
data class ChatMessageResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("sender") val sender: String?,
    @SerializedName("text") val text: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class ChatSessionResponse(
    @SerializedName("session_id") val sessionId: String?,
    @SerializedName("is_active") val isActive: Boolean?,
    @SerializedName("messages") val messages: List<ChatMessageResponse>?
)

data class ChatReplyResponse(
    @SerializedName("session_id") val sessionId: String?,
    @SerializedName("reply_text") val replyText: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class ChatSummaryResponse(
    @SerializedName("draft_summary") val draftSummary: String?
)

// REQUEST MODELS (Dari Aplikasi ke Server)
data class SendMessageRequest(
    @SerializedName("session_id") val sessionId: String?,
    @SerializedName("message") val message: String
)

data class CloseSessionRequest(
    @SerializedName("final_summary") val finalSummary: String
)