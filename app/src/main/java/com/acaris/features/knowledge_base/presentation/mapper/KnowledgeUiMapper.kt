package com.acaris.features.knowledge_base.presentation.mapper

import com.acaris.features.knowledge_base.domain.model.KnowledgeDocument
import com.acaris.features.knowledge_base.presentation.model.KnowledgeUiModel

fun KnowledgeDocument.toUiModel(): KnowledgeUiModel {
    return KnowledgeUiModel(
        id = this.id,
        title = this.title,
        fileName = this.fileName,
        fileUrl = this.fileUrl,
        category = this.category,
        uploadedAt = this.uploadedAt,
        updatedAt = this.updatedAt
    )
}