package com.acaris.features.knowledge_base.domain.usecase

import com.acaris.features.knowledge_base.domain.repository.KnowledgeRepository
import javax.inject.Inject

class GetKnowledgeDocumentsUseCase @Inject constructor(
    private val repository: KnowledgeRepository
) {
    suspend operator fun invoke(category: String? = null, search: String? = null) =
        repository.getKnowledgeDocuments(category, search)
}