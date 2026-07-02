package com.acaris.features.monitoring_mahasiswa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.chatbot.presentation.mapper.toPresentation
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetDaftarMahasiswaUseCase
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetDetailMahasiswaUseCase
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetRiwayatBimbinganUseCase
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetMahasiswaChatbotHistoryUseCase
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetMahasiswaChatbotDetailUseCase
import com.acaris.features.monitoring_mahasiswa.presentation.mapper.toUiModel
import com.acaris.features.monitoring_mahasiswa.presentation.model.MahasiswaBimbinganUiModel
import com.acaris.features.monitoring_mahasiswa.presentation.model.MonitoringUiState
import com.acaris.features.monitoring_mahasiswa.presentation.model.SortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val getDaftarMahasiswaUseCase: GetDaftarMahasiswaUseCase,
    private val getDetailMahasiswaUseCase: GetDetailMahasiswaUseCase,
    private val getRiwayatBimbinganUseCase: GetRiwayatBimbinganUseCase,
    private val getMahasiswaChatbotHistoryUseCase: GetMahasiswaChatbotHistoryUseCase,
    private val getMahasiswaChatbotDetailUseCase: GetMahasiswaChatbotDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    private fun getFilteredAndSortedList(
        rawList: List<MahasiswaBimbinganUiModel>,
        query: String,
        sort: SortOption
    ): List<MahasiswaBimbinganUiModel> {
        val filtered = if (query.isBlank()) {
            rawList
        } else {
            rawList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.npm.contains(query, ignoreCase = true)
            }
        }

        return when (sort) {
            SortOption.NAMA_AZ -> filtered.sortedBy { it.name.lowercase() }
            SortOption.NPM_ASC -> filtered.sortedBy { it.npm }
        }
    }

    fun fetchDaftarMahasiswa() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getDaftarMahasiswaUseCase()

            result.fold(
                onSuccess = { domainList ->
                    val uiList = domainList.map { it.toUiModel() }

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            listMahasiswa = uiList,
                            filteredListMahasiswa = getFilteredAndSortedList(
                                rawList = uiList,
                                query = state.searchQuery,
                                sort = state.sortOption
                            )
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Terjadi kesalahan") }
                }
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredListMahasiswa = getFilteredAndSortedList(
                    rawList = state.listMahasiswa,
                    query = query,
                    sort = state.sortOption
                )
            )
        }
    }

    fun onSortOptionChanged(option: SortOption) {
        _uiState.update { state ->
            state.copy(
                sortOption = option,
                filteredListMahasiswa = getFilteredAndSortedList(
                    rawList = state.listMahasiswa,
                    query = state.searchQuery,
                    sort = option
                )
            )
        }
    }

    fun fetchDetailMahasiswa(mahasiswaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, detailMahasiswa = null) }
            val result = getDetailMahasiswaUseCase(mahasiswaId)

            result.fold(
                onSuccess = { domainDetail ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            detailMahasiswa = domainDetail.toUiModel()
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat detail") }
                }
            )
        }
    }

    fun fetchHistoryBimbingan(mahasiswaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getRiwayatBimbinganUseCase(mahasiswaId)

            result.fold(
                onSuccess = { domainList ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            historyList = domainList.map { it.toUiModel() }
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat riwayat bimbingan") }
                }
            )
        }
    }

    fun fetchChatbotHistory(mahasiswaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getMahasiswaChatbotHistoryUseCase(mahasiswaId)

            result.fold(
                onSuccess = { domainList ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            historyChatbotList = domainList.map { it.toPresentation() }
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat riwayat chatbot") }
                }
            )
        }
    }

    fun fetchChatbotDetail(mahasiswaId: String, sessionId: String) {
        val selectedItem = _uiState.value.historyChatbotList.find { it.sessionId == sessionId }
        val summary = selectedItem?.title ?: "Sesi Bimbingan Akademik"

        viewModelScope.launch {
            _uiState.update { it.copy(
                isLoading = true,
                errorMessage = null,
                chatbotDetailSessionMessages = emptyList(),
                chatbotDetailSummary = summary
            )}

            val result = getMahasiswaChatbotDetailUseCase(mahasiswaId, sessionId)

            result.fold(
                onSuccess = { domainSession ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            chatbotDetailSessionMessages = domainSession.messages.map { it.toPresentation() }
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat detail obrolan") }
                }
            )
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