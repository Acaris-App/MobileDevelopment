package com.acaris.features.monitoring_mahasiswa.presentation.model

data class RiwayatBimbinganUiModel(
    val id: String,
    val displayDate: String,
    val displayTime: String,
    val agenda: String,
    val rawStatus: String,
    val statusLabel: String,
    val keteranganDosen: String
)

data class MonitoringUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val listMahasiswa: List<MahasiswaBimbinganUiModel> = emptyList(),
    val detailMahasiswa: DetailMahasiswaUiModel? = null,
    val historyList: List<RiwayatBimbinganUiModel> = emptyList()
)
