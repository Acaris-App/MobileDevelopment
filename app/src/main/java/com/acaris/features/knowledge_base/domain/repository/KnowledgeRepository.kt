package com.acaris.features.knowledge_base.domain.repository

import com.acaris.features.knowledge_base.domain.model.KnowledgeDocument
import java.io.File

interface KnowledgeRepository {
    suspend fun getKnowledgeDocuments(category: String? = null, search: String? = null): Result<List<KnowledgeDocument>>
    suspend fun uploadKnowledgeDocument(title: String, category: String, file: File): Result<KnowledgeDocument>
    suspend fun updateKnowledgeDocument(id: String, title: String?, category: String?, file: File?): Result<KnowledgeDocument>
    suspend fun deleteKnowledgeDocument(id: String): Result<Unit>
}