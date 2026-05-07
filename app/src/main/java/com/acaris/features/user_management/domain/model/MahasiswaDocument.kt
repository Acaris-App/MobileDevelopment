package com.acaris.features.user_management.domain.model

data class MahasiswaDocument(
    val id: String,
    val documentType: String,
    val semester: Int,
    val filePath: String,
    val uploadedAt: String
)