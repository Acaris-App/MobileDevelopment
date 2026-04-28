package com.acaris.features.monitoring_mahasiswa.domain.model

data class DokumenBimbingan(
    val id: String,
    val type: String,
    val semester: Int?,
    val fileUrl: String,
    val uploadedAt: String
)