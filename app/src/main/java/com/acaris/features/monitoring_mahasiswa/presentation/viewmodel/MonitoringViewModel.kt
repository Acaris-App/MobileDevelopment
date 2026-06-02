package com.acaris.features.monitoring_mahasiswa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.chatbot.presentation.mapper.toPresentation // 🌟 MAPPER DARI CHATBOT
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetDaftarMahasiswaUseCase
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetDetailMahasiswaUseCase
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetRiwayatBimbinganUseCase
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetMahasiswaChatbotHistoryUseCase // 🌟 USE CASE BARU
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetMahasiswaChatbotDetailUseCase // 🌟 USE CASE BARU
import com.acaris.features.monitoring_mahasiswa.presentation.mapper.toUiModel
import com.acaris.features.monitoring_mahasiswa.presentation.model.MonitoringUiState
import com.acaris.features.monitoring_mahasiswa.presentation.model.SortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val getDaftarMahasiswaUseCase: GetDaftarMahasiswaUseCase,
    private val getDetailMahasiswaUseCase: GetDetailMahasiswaUseCase,
    private val getRiwayatBimbinganUseCase: GetRiwayatBimbinganUseCase,
    private val getMahasiswaChatbotHistoryUseCase: GetMahasiswaChatbotHistoryUseCase, // 🌟 INJECT INI
    private val getMahasiswaChatbotDetailUseCase: GetMahasiswaChatbotDetailUseCase // 🌟 INJECT INI
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    fun fetchDaftarMahasiswa() {
        viewModelScope.launch {
            getDaftarMahasiswaUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true, errorMessage = null) } }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Terjadi kesalahan") }
                }
                .collect { domainList ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            listMahasiswa = domainList.map { it.toUiModel() }
                        )
                    }
                    applySearchAndSort()
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applySearchAndSort()
    }

    fun onSortOptionChanged(option: SortOption) {
        _uiState.update { it.copy(sortOption = option) }
        applySearchAndSort()
    }

    private fun applySearchAndSort() {
        _uiState.update { state ->
            var result = if (state.searchQuery.isBlank()) {
                state.listMahasiswa
            } else {
                state.listMahasiswa.filter {
                    it.name.contains(state.searchQuery, ignoreCase = true) ||
                            it.npm.contains(state.searchQuery, ignoreCase = true)
                }
            }

            result = when (state.sortOption) {
                SortOption.NAMA_AZ -> result.sortedBy { it.name.lowercase() }
                SortOption.NPM_ASC -> result.sortedBy { it.npm }
            }

            state.copy(filteredListMahasiswa = result)
        }
    }

    fun fetchDetailMahasiswa(mahasiswaId: String) {
        viewModelScope.launch {
            getDetailMahasiswaUseCase(mahasiswaId)
                .onStart {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null, detailMahasiswa = null) }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat detail") }
                }
                .collect { domainDetail ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            detailMahasiswa = domainDetail.toUiModel()
                        )
                    }
                }
        }
    }

    fun fetchHistoryBimbingan(mahasiswaId: String) {
        viewModelScope.launch {
            getRiwayatBimbinganUseCase(mahasiswaId)
                .onStart {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat riwayat bimbingan") }
                }
                .collect { domainList ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            historyList = domainList.map { it.toUiModel() }
                        )
                    }
                }
        }
    }

    // 🌟 FUNGSI BARU 1: FETCH DAFTAR RIWAYAT CHATBOT
    fun fetchChatbotHistory(mahasiswaId: String) {
        viewModelScope.launch {
            getMahasiswaChatbotHistoryUseCase(mahasiswaId)
                .onStart {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat riwayat chatbot") }
                }
                .collect { domainList ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            // Kita pakai toPresentation() milik mapper Chatbot
                            historyChatbotList = domainList.map { it.toPresentation() }
                        )
                    }
                }
        }
    }

    // 🌟 FUNGSI BARU 2: FETCH DETAIL OBROLAN CHATBOT TERTENTU
    fun fetchChatbotDetail(mahasiswaId: String, sessionId: String) {
        // Cari judul/summary dari list yang ada untuk ditampilkan di pop-up
        val selectedItem = _uiState.value.historyChatbotList.find { it.sessionId == sessionId }
        val summary = selectedItem?.title ?: "Sesi Bimbingan Akademik"

        viewModelScope.launch {
            getMahasiswaChatbotDetailUseCase(mahasiswaId, sessionId)
                .onStart {
                    _uiState.update { it.copy(
                        isLoading = true,
                        errorMessage = null,
                        chatbotDetailSessionMessages = emptyList(), // Reset sebelum load
                        chatbotDetailSummary = summary
                    )}
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat detail obrolan") }
                }
                .collect { domainSession ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            // Kita pakai toPresentation() milik mapper Chatbot
                            chatbotDetailSessionMessages = domainSession.messages.map { it.toPresentation() }
                        )
                    }
                }
        }
    }

    fun clearChatbotDetail() {
        _uiState.update { it.copy(
            chatbotDetailSessionMessages = emptyList(),
            chatbotDetailSummary = ""
        )}
    }

    fun resetError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}