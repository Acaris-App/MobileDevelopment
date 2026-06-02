package com.acaris.features.monitoring_mahasiswa.presentation.model

import com.acaris.features.chatbot.presentation.model.ChatHistoryItemUiModel
import com.acaris.features.chatbot.presentation.model.ChatMessageUiModel

data class RiwayatBimbinganUiModel(
    val id: String,
    val displayDate: String,
    val displayTime: String,
    val agenda: String,
    val rawStatus: String,
    val statusLabel: String,
    val keteranganDosen: String
)

enum class SortOption(val label: String) {
    NAMA_AZ("Nama (A-Z)"),
    NPM_ASC("NPM (Kecil ke Besar)")
}

data class MonitoringUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val listMahasiswa: List<MahasiswaBimbinganUiModel> = emptyList(),
    val filteredListMahasiswa: List<MahasiswaBimbinganUiModel> = emptyList(),
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.NAMA_AZ,
    val detailMahasiswa: DetailMahasiswaUiModel? = null,
    val historyList: List<RiwayatBimbinganUiModel> = emptyList(),
    val historyChatbotList: List<ChatHistoryItemUiModel> = emptyList(),
    val chatbotDetailSessionMessages: List<ChatMessageUiModel> = emptyList(),
    val chatbotDetailSummary: String = ""
)