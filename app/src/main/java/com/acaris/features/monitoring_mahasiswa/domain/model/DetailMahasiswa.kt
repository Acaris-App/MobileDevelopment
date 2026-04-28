package com.acaris.features.monitoring_mahasiswa.domain.model

data class DetailMahasiswa(
    val id: String,
    val name: String,
    val npm: String,
    val email: String,
    val profilePictureUrl: String?,
    val angkatan: Int?,
    val ipk: String?,
    val currentSemester: Int?,
    val kodeKelas: String?,
    val dokumen: List<DokumenBimbingan>
)