package com.acaris.features.dashboard.presentation.model

import com.acaris.features.schedule.presentation.model.ScheduleStatus
import java.time.LocalDate

data class DashboardDosenUiModel(
    val namaDosen: String,
    val nipDosen: String,
    val fotoDosen: String,
    val kodeKelas: String,
    val jumlahMahasiswaBimbingan: String,
    val bimbinganHariIni: String,
    val bimbinganSemesterIni: String,
    val jadwalMingguIni: List<JadwalMingguIniUiModel>,
    val kalenderBimbinganMap: Map<LocalDate, ScheduleStatus>
) {
    // 🌟 TEMPLATE DATA KOSONG (Default)
    companion object {
        fun empty() = DashboardDosenUiModel(
            namaDosen = "Dosen Pembimbing",
            nipDosen = "-",
            fotoDosen = "",
            kodeKelas = "-",
            jumlahMahasiswaBimbingan = "0",
            bimbinganHariIni = "0",
            bimbinganSemesterIni = "0",
            jadwalMingguIni = emptyList(),
            kalenderBimbinganMap = emptyMap()
        )
    }
}

data class JadwalMingguIniUiModel(
    val id: String,
    val date: String,
    val waktu: String,
    val status: ScheduleStatus,
    val keterangan: String,
    val listMahasiswa: List<MahasiswaBookingUiModel>
)

data class MahasiswaBookingUiModel(
    val nama: String,
    val npm: String,
    val agenda: String
)

data class DashboardDosenState(
    val isLoading: Boolean = false,
    val dashboardData: DashboardDosenUiModel? = null,
    val errorMessage: String? = null
)