package com.acaris.features.chatbot.data.remote.model

import com.google.gson.annotations.SerializedName

data class ChatHistoryItemResponse(
    @SerializedName("session_id") val sessionId: String?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("status") val status: String?
)