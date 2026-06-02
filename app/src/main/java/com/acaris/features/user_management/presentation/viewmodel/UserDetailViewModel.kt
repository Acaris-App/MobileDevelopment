package com.acaris.features.user_management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.user_management.domain.usecase.UserManagementUseCases
import com.acaris.features.user_management.presentation.mapper.toUiModel
import com.acaris.features.user_management.presentation.model.UserDetailUiState
import com.acaris.features.chatbot.presentation.mapper.toPresentation // 🌟 IMPORT MAPPER CHATBOT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val useCases: UserManagementUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserDetailUiState())
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    fun loadUserDetail(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val userResult = useCases.getUserDetail(userId)
            val docResult = useCases.getMahasiswaDocuments(userId)
            val bimbinganResult = useCases.getBimbinganHistory(userId)

            // 🌟 LANGSUNG AMBIL LIST CHATBOT JUGA SAAT HALAMAN DIBUKA
            val chatbotResult = useCases.getAdminMahasiswaChatbotHistory(userId)

            val allDocs = docResult.getOrDefault(emptyList()).map { it.toUiModel() }
            val krs = allDocs.filter { it.documentType.lowercase() == "krs" }
            val khs = allDocs.filter { it.documentType.lowercase() == "khs" }
            val transkrip = allDocs.filter { it.documentType.lowercase() == "transkrip" }

            val bimbingan = bimbinganResult.getOrDefault(emptyList()).map { it.toUiModel() }

            // 🌟 GUNAKAN MAPPER DARI CHATBOT
            val chatbotHistory = chatbotResult.getOrDefault(emptyList()).map { it.toPresentation() }

            val isTotalFailure = docResult.isFailure && bimbinganResult.isFailure
            val errorMsg = if (isTotalFailure) "Terjadi kesalahan saat memuat semua data." else null

            _uiState.update {
                it.copy(
                    isLoading = false,
                    user = userResult.getOrNull()?.toUiModel(),
                    bimbinganHistory = bimbingan,
                    krsDocuments = krs,
                    khsDocuments = khs,
                    transkripDocuments = transkrip,
                    chatbotHistoryList = chatbotHistory, // 🌟 UPDATE STATE CHATBOT
                    errorMessage = errorMsg
                )
            }
        }
    }

    // 🌟 FUNGSI BARU: MENGAMBIL ISI PERCAKAPAN CHATBOT
    fun fetchChatbotDetail(userId: String, sessionId: String) {
        val selectedItem = _uiState.value.chatbotHistoryList.find { it.sessionId == sessionId }
        val summary = selectedItem?.title ?: "Sesi Bimbingan Akademik"

        viewModelScope.launch {
            _uiState.update { it.copy(
                isLoading = true,
                errorMessage = null,
                chatbotDetailMessages = emptyList(), // Reset
                chatbotDetailSummary = summary
            )}

            val result = useCases.getAdminMahasiswaChatbotDetail(userId, sessionId)

            if (result.isSuccess) {
                val domainSession = result.getOrNull()
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        // Gunakan mapper dari fitur Chatbot
                        chatbotDetailMessages = domainSession?.messages?.map { it.toPresentation() } ?: emptyList()
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun clearChatbotDetail() {
        _uiState.update { it.copy(
            chatbotDetailMessages = emptyList(),
            chatbotDetailSummary = ""
        )}
    }

    fun uploadDocument(userId: String, documentType: String, semester: Int?, file: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = useCases.uploadMahasiswaDocument(userId, documentType, semester, file)
            if (result.isSuccess) {
                _uiState.update { it.copy(successMessage = "Dokumen berhasil diunggah!") }
                loadUserDetail(userId)
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun updateDocument(userId: String, documentId: String, semester: Int?, file: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = useCases.updateMahasiswaDocument(documentId, semester, file)
            if (result.isSuccess) {
                _uiState.update { it.copy(successMessage = "Dokumen berhasil diperbarui!") }
                loadUserDetail(userId)
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun deleteDocument(userId: String, documentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = useCases.deleteMahasiswaDocument(documentId)
            if (result.isSuccess) {
                _uiState.update { it.copy(successMessage = "Dokumen berhasil dihapus!") }
                loadUserDetail(userId)
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}