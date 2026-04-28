package com.acaris.features.monitoring_mahasiswa.domain.model

data class MahasiswaBimbingan(
    val id: String,
    val name: String,
    val npm: String,
    val profilePictureUrl: String?,
    val angkatan: Int?,
    val currentSemester: Int?
)