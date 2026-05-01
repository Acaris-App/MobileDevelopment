package com.acaris.features.monitoring_mahasiswa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetDaftarMahasiswaUseCase
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetDetailMahasiswaUseCase
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetRiwayatBimbinganUseCase
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
    private val getRiwayatBimbinganUseCase: GetRiwayatBimbinganUseCase
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

            // 2. Lakukan Pengurutan (Sort)
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

    fun resetError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}