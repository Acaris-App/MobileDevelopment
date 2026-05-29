package com.acaris.features.chatbot.domain.model

data class ChatHistoryDomain(
    val sessionId: String,
    val title: String,      // Akan diisi dari ringkasan (summary)
    val createdAt: String,  // Waktu sesi dibuat
    val status: String      // Misal: "active" atau "completed"
)