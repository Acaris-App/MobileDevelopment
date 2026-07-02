package com.acaris.features.dashboard.presentation.model

import com.acaris.features.schedule.presentation.model.ScheduleStatus
import java.time.LocalDate

data class DashboardMahasiswaUiModel(
    val namaMahasiswa: String,
    val npmMahasiswa: String,
    val fotoMahasiswa: String,
    val dosenPa: String,
    val fotoDosen: String,
    val nipDosen: String,
    val ipk: String,
    val semesterSaatIni: String,
    val bimbinganSemesterIni: String,
    val bimbinganKeseluruhan: String,
    val chatbotBulanIni: String,
    val jadwalTerdekat: List<JadwalSingkatUiModel>,
    val kalenderBimbinganMap: Map<LocalDate, ScheduleStatus>,
) {
    companion object {
        fun empty() = DashboardMahasiswaUiModel(
            namaMahasiswa = "Mahasiswa",
            npmMahasiswa = "-",
            fotoMahasiswa = "",
            dosenPa = "-",
            fotoDosen = "",
            nipDosen = "-",
            ipk = "0.00",
            semesterSaatIni = "0",
            bimbinganSemesterIni = "0",
            bimbinganKeseluruhan = "0",
            chatbotBulanIni = "0",
            jadwalTerdekat = emptyList(),
            kalenderBimbinganMap = emptyMap(),
        )
    }
}

data class JadwalSingkatUiModel(
    val id: String,
    val date: String,
    val waktu: String,
    val agenda: String,
    val status: ScheduleStatus,
    val keterangan: String
)

data class DashboardMahasiswaState(
    val isLoading: Boolean = false,
    val dashboardData: DashboardMahasiswaUiModel? = null,
    val errorMessage: String? = null
)