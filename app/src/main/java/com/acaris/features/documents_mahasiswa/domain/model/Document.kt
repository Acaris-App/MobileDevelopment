package com.acaris.features.documents_mahasiswa.domain.model

data class Document(
    val id: String,
    val type: String,
    val semester: Int?,
    val fileUrl: String,
    val uploadedAt: String
)