package com.acaris.features.chatbot.presentation.model

data class ChatMessageUiModel(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val time: String
)

data class ChatbotUiState(
    val isLoading: Boolean = false, // Untuk loading awal cek sesi
    val isSending: Boolean = false, // Untuk indikator "AI sedang mengetik..."
    val errorMessage: String? = null,
    val sessionId: String? = null, // Jika null = tidak ada sesi aktif
    val messages: List<ChatMessageUiModel> = emptyList(),

    // State untuk fitur Akhiri Sesi (Rangkuman)
    val isGeneratingSummary: Boolean = false,
    val showSummaryDialog: Boolean = false,
    val draftSummary: String = ""
)