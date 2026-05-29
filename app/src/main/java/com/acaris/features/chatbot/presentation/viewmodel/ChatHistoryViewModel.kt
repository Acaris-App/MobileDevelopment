package com.acaris.features.chatbot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.chatbot.domain.usecase.GetChatHistoryDetailUseCase
import com.acaris.features.chatbot.domain.usecase.GetChatHistoryUseCase
import com.acaris.features.chatbot.presentation.mapper.toPresentation
import com.acaris.features.chatbot.presentation.model.ChatbotHistoryUiState
import com.acaris.features.chatbot.presentation.model.ChatHistoryItemUiModel
import com.acaris.features.chatbot.presentation.model.ChatMessageUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    // 🌟 1. FUNGSI UNTUK HALAMAN LIST (DENGAN DUMMY DATA)
    fun loadHistory() {
        _uiState.update { it.copy(isLoadingList = true, errorMessage = null) }

        viewModelScope.launch {
            // Simulasi jeda jaringan selama 1 detik biar ada efek loading-nya
            delay(1000)

            // 🌟 DUMMY DATA LIST RIWAYAT
            val dummyHistoryList = listOf(
                ChatHistoryItemUiModel(
                    sessionId = "S-001",
                    title = "Membahas penerapan Clean Architecture dan MVVM pada Acaris",
                    date = "Kamis, 28 Mei 2026",
                    status = "completed"
                ),
                ChatHistoryItemUiModel(
                    sessionId = "S-002",
                    title = "Revisi proposal: Integrasi Chatbot Menggunakan Metode SCRUM",
                    date = "Senin, 25 Mei 2026",
                    status = "completed"
                ),
                ChatHistoryItemUiModel(
                    sessionId = "S-003",
                    title = "Pertanyaan seputar format pendaftaran seminar proposal",
                    date = "Rabu, 20 Mei 2026",
                    status = "completed"
                )
            )

            _uiState.update { state ->
                state.copy(
                    isLoadingList = false,
                    historyList = dummyHistoryList
                )
            }

            /* TODO: BUKA KOMENTAR KODE DI BAWAH INI JIKA API BACKEND SUDAH SIAP
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
            */
        }
    }

    // 🌟 2. FUNGSI UNTUK HALAMAN DETAIL (DENGAN DUMMY DATA)
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
            // Simulasi jeda jaringan
            delay(1000)

            // 🌟 DUMMY DATA ISI CHAT BUBBLE
            val dummyMessages = when (sessionId) {
                "S-001" -> listOf(
                    ChatMessageUiModel("msg1", "Bagaimana cara memisahkan layer di Clean Architecture?", isFromUser = true, time = "10:00"),
                    ChatMessageUiModel("msg2", "Di Clean Architecture, Anda sebaiknya membaginya menjadi 3 layer utama: Domain, Data, dan Presentation (UI).", isFromUser = false, time = "10:01"),
                    ChatMessageUiModel("msg3", "Lalu untuk view model taruh di mana?", isFromUser = true, time = "10:03"),
                    ChatMessageUiModel("msg4", "ViewModel diletakkan di layer Presentation. Layer ini bertugas menghubungkan UI (Jetpack Compose) dengan UseCase yang ada di layer Domain.", isFromUser = false, time = "10:04")
                )
                "S-002" -> listOf(
                    ChatMessageUiModel("msg5", "Apa saja tahapan dalam metode SCRUM?", isFromUser = true, time = "14:20"),
                    ChatMessageUiModel("msg6", "Tahapan utama dalam SCRUM meliputi Product Backlog, Sprint Planning, Daily Scrum, Sprint Review, dan Sprint Retrospective.", isFromUser = false, time = "14:21")
                )
                else -> listOf(
                    ChatMessageUiModel("msg7", "Halo, apa syarat daftar sempro?", isFromUser = true, time = "09:15"),
                    ChatMessageUiModel("msg8", "Syarat pendaftaran seminar proposal antara lain: telah menyelesaikan minimal 110 SKS, melampirkan KRS terakhir, dan lembar persetujuan dosen pembimbing.", isFromUser = false, time = "09:16")
                )
            }

            _uiState.update { state ->
                state.copy(
                    isLoadingDetail = false,
                    selectedSessionMessages = dummyMessages
                )
            }

            /* TODO: BUKA KOMENTAR KODE DI BAWAH INI JIKA API BACKEND SUDAH SIAP
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
            */
        }
    }

    // 🌟 3. FUNGSI BANTUAN UI (TETAP SAMA)
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