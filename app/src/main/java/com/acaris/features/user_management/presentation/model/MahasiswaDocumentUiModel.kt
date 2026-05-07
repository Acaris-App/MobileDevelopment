package com.acaris.features.user_management.presentation.model

data class MahasiswaDocumentUiModel(
    val id: String,
    val documentType: String,
    val semester: Int,
    val filePath: String,
    val uploadedAt: String
)