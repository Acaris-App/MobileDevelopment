package com.acaris.features.knowledge_base.data.mapper

import com.acaris.features.knowledge_base.data.remote.model.KnowledgeDocumentResponse
import com.acaris.features.knowledge_base.domain.model.KnowledgeDocument

fun KnowledgeDocumentResponse.toDomain(): KnowledgeDocument {
    return KnowledgeDocument(
        id = this.id.orEmpty(),
        title = this.title ?: "Dokumen Tanpa Judul",
        fileName = this.fileName ?: "unknown_file.pdf",
        fileUrl = this.fileUrl.orEmpty(),
        category = this.category ?: "Lainnya",
        uploadedAt = this.uploadedAt?.substringBefore("T").orEmpty(),
        updatedAt = this.updatedAt?.substringBefore("T").orEmpty()
    )
}