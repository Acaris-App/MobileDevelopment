package com.acaris.features.user_management.presentation.model

import com.acaris.features.chatbot.presentation.model.ChatHistoryItemUiModel
import com.acaris.features.chatbot.presentation.model.ChatMessageUiModel

data class UserDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val user: UserUiModel? = null,
    val bimbinganHistory: List<BimbinganHistoryUiModel> = emptyList(),
    val krsDocuments: List<MahasiswaDocumentUiModel> = emptyList(),
    val khsDocuments: List<MahasiswaDocumentUiModel> = emptyList(),
    val transkripDocuments: List<MahasiswaDocumentUiModel> = emptyList(),
    val chatbotHistoryList: List<ChatHistoryItemUiModel> = emptyList(),
    val chatbotDetailMessages: List<ChatMessageUiModel> = emptyList(),
    val chatbotDetailSummary: String = ""
)