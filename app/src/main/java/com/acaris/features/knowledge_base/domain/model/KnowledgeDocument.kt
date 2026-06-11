package com.acaris.features.knowledge_base.domain.model

data class KnowledgeDocument(
    val id: String,
    val title: String,
    val fileName: String,
    val fileUrl: String,
    val category: String, // "Peraturan Akademik", "Jadwal", "Kurikulum", "Peraturan Rektor", "Kalender Akademik"
    val uploadedAt: String,
    val updatedAt: String
)