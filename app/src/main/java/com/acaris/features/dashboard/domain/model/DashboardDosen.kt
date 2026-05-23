package com.acaris.features.dashboard.domain.model

data class DashboardDosen(
    val namaDosen: String,
    val nipDosen: String,
    val fotoDosen: String,
    val kodeKelas: String,
    val jumlahMahasiswaBimbingan: Int,
    val bimbinganHariIni: Int,
    val bimbinganSemesterIni: Int,
    val jadwalMingguIni: List<JadwalMingguIniDosenDomain>,
    val kalenderBimbingan: List<KalenderItemDomain>
)

data class JadwalMingguIniDosenDomain(
    val id: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val keterangan: String,
    val listMahasiswa: List<MahasiswaBookingDomain>
)

data class MahasiswaBookingDomain(
    val nama: String,
    val npm: String,
    val agenda: String
)