package com.acaris.features.monitoring_mahasiswa.presentation.model

data class MahasiswaBimbinganUiModel(
    val id: String,
    val name: String,
    val npm: String,
    val profilePictureUrl: String,
    val infoAkademik: String // 🌟 Hasil gabungan Angkatan & Semester
)

data class DokumenBimbinganUiModel(
    val id: String,
    val title: String, // 🌟 Contoh: "KRS Semester 6"
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
    val infoAkademik: String,
    val kodeKelas: String,
    val dokumen: List<DokumenBimbinganUiModel>
)