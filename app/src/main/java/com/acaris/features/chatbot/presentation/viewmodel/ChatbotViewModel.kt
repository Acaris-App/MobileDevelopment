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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat // 🌟 Tambahan import
import java.util.Date // 🌟 Tambahan import
import java.util.Locale // 🌟 Tambahan import
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val getActiveSessionUseCase: GetActiveSessionUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val generateSummaryUseCase: GenerateChatSummaryUseCase,
    private val closeSessionUseCase: CloseChatSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatbotUiState())
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    // FUNGSI BANTUAN UNTUK MENDAPATKAN JAM SAAT INI DI HP USER
    private fun getCurrentTimeStr(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    fun loadActiveSession() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
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
                        // Tidak ada sesi aktif
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

        // Simpan ID pesan untuk dicari saat mau di-update
        val tempMessageId = UUID.randomUUID().toString()

        // 1. Tambahkan pesan user ke layar secara instan
        val userMessage = ChatMessageUiModel(
            id = tempMessageId,
            text = text,
            isFromUser = true,
            time = "Memuat..." // Masih loading
        )

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                isSending = true, // Munculkan indikator "Aca sedang mengetik..."
                errorMessage = null
            )
        }

        // 2. Kirim ke Server
        viewModelScope.launch {
            val result = sendChatMessageUseCase(currentSessionId, text)
            result.fold(
                onSuccess = { reply ->
                    // 🌟 FIX UTAMA BUG "MEMUAT...":
                    // Kita buat list baru, cari pesan user tadi, dan ganti "Memuat..." dengan jam HP saat ini.
                    _uiState.update { state ->
                        val updatedMessages = state.messages.map { msg ->
                            if (msg.id == tempMessageId) {
                                msg.copy(time = getCurrentTimeStr()) // Hapus Memuat..., ganti Jam Asli
                            } else {
                                msg
                            }
                        }

                        state.copy(
                            isSending = false,
                            sessionId = reply.sessionId,
                            // Gabungkan list yang sudah di-update dengan balasan dari bot
                            messages = updatedMessages + reply.replyMessage.toPresentation()
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        // Jika gagal, ganti tulisan Memuat menjadi tulisan error (opsional)
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

    // 🌟 Sesuai perintah Kapten: Ganti nama fungsi trigger Akhiri sesi
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