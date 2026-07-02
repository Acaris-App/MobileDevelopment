package com.acaris.features.chatbot.presentation.model

data class ChatHistoryItemUiModel(
    val sessionId: String,
    val title: String,
    val date: String,
    val status: String
)

data class ChatbotHistoryUiState(
    val isLoadingList: Boolean = false,
    val historyList: List<ChatHistoryItemUiModel> = emptyList(),

    val isLoadingDetail: Boolean = false,
    val selectedSessionSummary: String = "",
    val selectedSessionMessages: List<ChatMessageUiModel> = emptyList(),
    val showSummaryDialog: Boolean = false,

    val errorMessage: String? = null
)