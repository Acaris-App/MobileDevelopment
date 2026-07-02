package com.acaris.features.monitoring_mahasiswa.presentation.model

data class MahasiswaBimbinganUiModel(
    val id: String,
    val name: String,
    val npm: String,
    val profilePictureUrl: String,
    val infoAkademik: String
)

data class DokumenBimbinganUiModel(
    val id: String,
    val title: String,
    val fileUrl: String,
    val uploadedAt: String
)

data class DetailMahasiswaUiModel(
    val id: String,
    val name: String,
    val npm: String,
    val email: String,
    val profilePictureUrl: String,
    val ipk: String,
    val angkatan: String,
    val semester: String,
    val kodeKelas: String,
    val dokumen: List<DokumenBimbinganUiModel>
)

data class RiwayatBimbinganUiModel(
    val id: String,
    val displayDate: String,
    val displayTime: String,
    val agenda: String,
    val rawStatus: String,
    val statusLabel: String,
    val keteranganDosen: String
)