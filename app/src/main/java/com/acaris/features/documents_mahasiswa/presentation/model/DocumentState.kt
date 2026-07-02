package com.acaris.features.documents_mahasiswa.presentation.model

data class SharedDocumentUiModel(
    val id: String,
    val type: String,
    val semester: Int?,
    val fileUrl: String,
    val uploadedAt: String = "-"
) {
    val displayTitle: String
        get() = if (semester != null && semester > 0) {
            "${type.uppercase()} Semester $semester"
        } else {
            type.uppercase()
        }
}

data class DocumentState(
    val isLoading: Boolean = false,
    val documents: List<SharedDocumentUiModel> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSuccessDelete: Boolean = false
)