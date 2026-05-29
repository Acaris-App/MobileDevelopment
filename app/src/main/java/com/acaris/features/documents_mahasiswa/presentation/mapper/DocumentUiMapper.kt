package com.acaris.features.documents_mahasiswa.presentation.mapper

import com.acaris.features.documents_mahasiswa.domain.model.Document
import com.acaris.features.documents_mahasiswa.presentation.model.SharedDocumentUiModel

fun Document.toUiModel(): SharedDocumentUiModel {
    return SharedDocumentUiModel(
        id = this.id,
        type = this.type,
        semester = this.semester,
        fileUrl = this.fileUrl,
        uploadedAt = this.uploadedAt ?: "-"
    )
}