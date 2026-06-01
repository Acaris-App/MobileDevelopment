package com.acaris.features.chatbot.presentation.model

data class ChatMessageUiModel(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val time: String
)

data class ChatbotUiState(
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val errorMessage: String? = null,
    val sessionId: String? = null,
    val messages: List<ChatMessageUiModel> = emptyList(),

    val isGeneratingSummary: Boolean = false,
    val showSummaryDialog: Boolean = false,
    val draftSummary: String = "",

    val isDocumentIncomplete: Boolean = false
)