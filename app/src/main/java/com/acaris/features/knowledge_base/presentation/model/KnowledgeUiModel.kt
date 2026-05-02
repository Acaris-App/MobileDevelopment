package com.acaris.features.knowledge_base.presentation.model

data class KnowledgeUiModel(
    val id: String,
    val title: String,
    val fileName: String,
    val fileUrl: String,
    val category: String,
    val uploadedAt: String,
    val updatedAt: String
)