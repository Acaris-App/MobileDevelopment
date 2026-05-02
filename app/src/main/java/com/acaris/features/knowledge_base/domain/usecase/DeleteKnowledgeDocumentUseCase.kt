package com.acaris.features.knowledge_base.domain.usecase

import com.acaris.features.knowledge_base.domain.repository.KnowledgeRepository
import javax.inject.Inject

class DeleteKnowledgeDocumentUseCase @Inject constructor(
    private val repository: KnowledgeRepository
) {
    suspend operator fun invoke(id: String) = repository.deleteKnowledgeDocument(id)
}