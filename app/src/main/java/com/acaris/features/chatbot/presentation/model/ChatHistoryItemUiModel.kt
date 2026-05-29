package com.acaris.features.chatbot.presentation.model

// Model untuk item di halaman List
data class ChatHistoryItemUiModel(
    val sessionId: String,
    val title: String,
    val date: String,
    val status: String
)

// Gabungan State untuk List dan Detail dimasukkan ke sini
data class ChatbotHistoryUiState(
    // 🌟 State untuk Halaman List (Daftar Riwayat)
    val isLoadingList: Boolean = false,
    val historyList: List<ChatHistoryItemUiModel> = emptyList(),

    // 🌟 State untuk Halaman Detail (Isi Chat)
    val isLoadingDetail: Boolean = false,
    val selectedSessionSummary: String = "",
    val selectedSessionMessages: List<ChatMessageUiModel> = emptyList(),
    val showSummaryDialog: Boolean = false,

    // 🌟 State Global
    val errorMessage: String? = null
)