package com.acaris.features.knowledge_base.presentation.model

data class KnowledgeUiState(
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val documents: List<KnowledgeUiModel> = emptyList(),
    val selectedCategory: String = "Semua",
    val searchQuery: String = "",
    val successMessage: String? = null,
    val errorMessage: String? = null
)