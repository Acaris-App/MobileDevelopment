package com.acaris.features.documents_mahasiswa.data.mapper

import com.acaris.features.documents_mahasiswa.data.remote.model.DocumentResponse
import com.acaris.features.documents_mahasiswa.domain.model.Document

fun DocumentResponse.toDomain(): Document {
    return Document(
        id = this.id.orEmpty(),
        type = this.documentType.orEmpty(),
        semester = this.semester,
        fileUrl = this.filePath.orEmpty(),
        uploadedAt = this.uploadedAt.orEmpty()
    )
}