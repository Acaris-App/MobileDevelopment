package com.acaris.features.dashboard.domain.model

data class DashboardMahasiswa(
    val namaMahasiswa: String,
    val npmMahasiswa: String,
    val fotoMahasiswa: String,
    val dosenPa: String,
    val fotoDosen: String,
    val nipDosen: String,
    val ipk: Double,
    val semesterSaatIni: Int,
    val bimbinganSemesterIni: Int,
    val bimbinganKeseluruhan: Int,
    val chatbotBulanIni: Int,
    val jadwalTerdekat: List<JadwalSingkatDomain>,
    val kalenderBimbingan: List<KalenderItemDomain>
)

data class JadwalSingkatDomain(
    val id: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val mahasiswaAgenda: String,
    val keterangan: String
)

data class KalenderItemDomain(
    val date: String,
    val status: String
)