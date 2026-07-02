package com.acaris.features.chatbot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.chatbot.domain.usecase.CloseChatSessionUseCase
import com.acaris.features.chatbot.domain.usecase.GenerateChatSummaryUseCase
import com.acaris.features.chatbot.domain.usecase.GetActiveSessionUseCase
import com.acaris.features.chatbot.domain.usecase.SendChatMessageUseCase
import com.acaris.features.chatbot.presentation.mapper.toPresentation
import com.acaris.features.chatbot.presentation.model.ChatMessageUiModel
import com.acaris.features.chatbot.presentation.model.ChatbotUiState
import com.acaris.features.profile.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val getActiveSessionUseCase: GetActiveSessionUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val generateSummaryUseCase: GenerateChatSummaryUseCase,
    private val closeSessionUseCase: CloseChatSessionUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatbotUiState())
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    private fun getCurrentTimeStr(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    fun checkDocumentAndLoadSession() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val profileResult = getProfileUseCase()

            profileResult.fold(
                onSuccess = { profile ->
                    if (!profile.isDokumenLengkap) {
                        _uiState.update { it.copy(isLoading = false, isDocumentIncomplete = true) }
                    } else {
                        loadActiveSession()
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal memverifikasi dokumen: ${error.message}") }
                }
            )
        }
    }

    private fun loadActiveSession() {
        viewModelScope.launch {
            val result = getActiveSessionUseCase()
            result.fold(
                onSuccess = { session ->
                    if (session != null && session.isActive) {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                sessionId = session.sessionId,
                                messages = session.messages.map { it.toPresentation() }
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, sessionId = null, messages = emptyList()) }
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val currentSessionId = _uiState.value.sessionId
        val tempMessageId = UUID.randomUUID().toString()

        val userMessage = ChatMessageUiModel(
            id = tempMessageId,
            text = text,
            isFromUser = true,
            time = "Memuat..."
        )

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                isSending = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val result = sendChatMessageUseCase(currentSessionId, text)
            result.fold(
                onSuccess = { reply ->
                    _uiState.update { state ->
                        val updatedMessages = state.messages.map { msg ->
                            if (msg.id == tempMessageId) {
                                msg.copy(time = getCurrentTimeStr())
                            } else {
                                msg
                            }
                        }

                        state.copy(
                            isSending = false,
                            sessionId = reply.sessionId,
                            messages = updatedMessages + reply.replyMessage.toPresentation()
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        val failedMessages = state.messages.map { msg ->
                            if (msg.id == tempMessageId) {
                                msg.copy(time = "Gagal kirim")
                            } else msg
                        }

                        state.copy(
                            isSending = false,
                            messages = failedMessages,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }

    fun endSession() {
        val currentSessionId = _uiState.value.sessionId ?: return

        _uiState.update { it.copy(isGeneratingSummary = true, errorMessage = null) }

        viewModelScope.launch {
            val result = generateSummaryUseCase(currentSessionId)
            result.fold(
                onSuccess = { summaryData ->
                    _uiState.update { state ->
                        state.copy(
                            isGeneratingSummary = false,
                            showSummaryDialog = true,
                            draftSummary = summaryData.draftSummary
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isGeneratingSummary = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun submitFinalSummary(finalText: String) {
        val currentSessionId = _uiState.value.sessionId ?: return

        _uiState.update { it.copy(isLoading = true, showSummaryDialog = false) }

        viewModelScope.launch {
            val result = closeSessionUseCase(currentSessionId, finalText)
            result.fold(
                onSuccess = {
                    _uiState.update { ChatbotUiState() }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, showSummaryDialog = true, errorMessage = error.message)
                    }
                }
            )
        }
    }

    fun dismissDialogs() {
        _uiState.update { it.copy(showSummaryDialog = false, errorMessage = null) }
    }
}