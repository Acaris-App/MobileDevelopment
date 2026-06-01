package com.acaris.features.chatbot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.chatbot.domain.usecase.GetChatHistoryDetailUseCase
import com.acaris.features.chatbot.domain.usecase.GetChatHistoryUseCase
import com.acaris.features.chatbot.presentation.mapper.toPresentation
import com.acaris.features.chatbot.presentation.model.ChatbotHistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatbotHistoryViewModel @Inject constructor(
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val getChatHistoryDetailUseCase: GetChatHistoryDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatbotHistoryUiState())
    val uiState: StateFlow<ChatbotHistoryUiState> = _uiState.asStateFlow()

    // 🌟 1. FUNGSI UNTUK HALAMAN LIST (API ASLI)
    fun loadHistory() {
        _uiState.update { it.copy(isLoadingList = true, errorMessage = null) }

        viewModelScope.launch {
            val result = getChatHistoryUseCase()
            result.fold(
                onSuccess = { domainList ->
                    _uiState.update { state ->
                        state.copy(
                            isLoadingList = false,
                            historyList = domainList.map { it.toPresentation() }
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoadingList = false, errorMessage = error.message) }
                }
            )
        }
    }

    // 🌟 2. FUNGSI UNTUK HALAMAN DETAIL (API ASLI)
    fun loadChatDetail(sessionId: String) {
        val selectedHistoryItem = _uiState.value.historyList.find { it.sessionId == sessionId }
        val foundSummary = selectedHistoryItem?.title ?: "Tidak ada ringkasan untuk sesi ini."

        _uiState.update { it.copy(
            isLoadingDetail = true,
            errorMessage = null,
            selectedSessionSummary = foundSummary,
            selectedSessionMessages = emptyList()
        ) }

        viewModelScope.launch {
            val result = getChatHistoryDetailUseCase(sessionId)
            result.fold(
                onSuccess = { sessionDomain ->
                    _uiState.update { state ->
                        state.copy(
                            isLoadingDetail = false,
                            selectedSessionMessages = sessionDomain.messages.map { it.toPresentation() }
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoadingDetail = false, errorMessage = error.message) }
                }
            )
        }
    }

    // 🌟 3. FUNGSI BANTUAN UI
    fun toggleSummaryDialog(show: Boolean) {
        _uiState.update { it.copy(showSummaryDialog = show) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearDetailState() {
        _uiState.update { it.copy(
            selectedSessionSummary = "",
            selectedSessionMessages = emptyList(),
            showSummaryDialog = false
        )}
    }
}