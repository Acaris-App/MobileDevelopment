package com.acaris.features.dashboard.presentation.model

data class DashboardAdminUiModel(
    val namaAdmin: String,
    val nipAdmin: String,
    val fotoAdmin: String,
    val totalMahasiswa: String,
    val totalDosen: String,
    val totalBimbingan: String,
    val totalChatbot: String,
    val topDosenBimbingan: List<TopDosenUiModel>,
    val topMahasiswaBimbingan: List<TopMahasiswaUiModel>,
    val topMahasiswaChatbot: List<TopMahasiswaUiModel>
) {
    // 🌟 TEMPLATE DATA KOSONG (Default)
    companion object {
        fun empty() = DashboardAdminUiModel(
            namaAdmin = "Admin Bimbingan",
            nipAdmin = "-",
            fotoAdmin = "",
            totalMahasiswa = "0",
            totalDosen = "0",
            totalBimbingan = "0",
            totalChatbot = "0",
            topDosenBimbingan = emptyList(),
            topMahasiswaBimbingan = emptyList(),
            topMahasiswaChatbot = emptyList()
        )
    }
}

data class TopDosenUiModel(
    val nama: String,
    val nip: String,
    val total: String
)

data class TopMahasiswaUiModel(
    val nama: String,
    val npm: String,
    val total: String
)

data class DashboardAdminState(
    val isLoading: Boolean = false,
    val dashboardData: DashboardAdminUiModel? = null,
    val errorMessage: String? = null
)