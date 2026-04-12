package com.acaris.features.documents_mahasiswa.presentation.model

import com.acaris.features.documents_mahasiswa.domain.model.Document

data class DocumentState(
    val isLoading: Boolean = false,
    val documents: List<Document> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSuccessDelete: Boolean = false
)