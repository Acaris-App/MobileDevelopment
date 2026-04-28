package com.acaris.features.monitoring_mahasiswa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetDaftarMahasiswaUseCase
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetDetailMahasiswaUseCase
import com.acaris.features.monitoring_mahasiswa.domain.usecase.GetRiwayatBimbinganUseCase
import com.acaris.features.monitoring_mahasiswa.presentation.mapper.toUiModel
import com.acaris.features.monitoring_mahasiswa.presentation.model.MonitoringUiState
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
                }
        }
    }

    fun fetchDetailMahasiswa(mahasiswaId: String) {
        viewModelScope.launch {
            getDetailMahasiswaUseCase(mahasiswaId)
                .onStart {
                    // Kita kosongkan detail lama saat memuat detail baru
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