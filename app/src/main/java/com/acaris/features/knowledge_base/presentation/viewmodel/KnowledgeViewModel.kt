package com.acaris.features.knowledge_base.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.knowledge_base.domain.usecase.DeleteKnowledgeDocumentUseCase
import com.acaris.features.knowledge_base.domain.usecase.GetKnowledgeDocumentsUseCase
import com.acaris.features.knowledge_base.domain.usecase.UpdateKnowledgeDocumentUseCase
import com.acaris.features.knowledge_base.domain.usecase.UploadKnowledgeDocumentUseCase
import com.acaris.features.knowledge_base.presentation.mapper.toUiModel
import com.acaris.features.knowledge_base.presentation.model.KnowledgeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class KnowledgeViewModel @Inject constructor(
    private val getKnowledgeDocumentsUseCase: GetKnowledgeDocumentsUseCase,
    private val uploadKnowledgeDocumentUseCase: UploadKnowledgeDocumentUseCase,
    private val updateKnowledgeDocumentUseCase: UpdateKnowledgeDocumentUseCase,
    private val deleteKnowledgeDocumentUseCase: DeleteKnowledgeDocumentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(KnowledgeUiState())
    val uiState: StateFlow<KnowledgeUiState> = _uiState.asStateFlow()

    init {
        fetchDocuments()
    }

    // ==========================================
    // 1. GET (AMBIL DATA DOKUMEN)
    // ==========================================
    fun fetchDocuments(category: String? = null, search: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val fetchCategory = if (category == "Semua") null else category
            val result = getKnowledgeDocumentsUseCase(fetchCategory, search)

            result.onSuccess { domainList ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        documents = domainList.map { it.toUiModel() }
                    )
                }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
            }
        }
    }

    // ==========================================
    // 2. CREATE (UPLOAD DOKUMEN BARU)
    // ==========================================
    fun uploadDocument(title: String, category: String, file: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, errorMessage = null, successMessage = null) }

            val result = uploadKnowledgeDocumentUseCase(title, category, file)

            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isUploading = false,
                        successMessage = "Berhasil mengunggah dokumen: $title"
                    )
                }
                fetchDocuments(_uiState.value.selectedCategory, _uiState.value.searchQuery)
            }.onFailure { exception ->
                _uiState.update { it.copy(isUploading = false, errorMessage = exception.message) }
            }
        }
    }

    // ==========================================
    // 3. UPDATE (EDIT DOKUMEN)
    // ==========================================
    fun updateDocument(id: String, title: String?, category: String?, file: File?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, errorMessage = null, successMessage = null) }

            val result = updateKnowledgeDocumentUseCase(id, title, category, file)

            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isUploading = false,
                        successMessage = "Berhasil memperbarui dokumen"
                    )
                }
                // Refresh list
                fetchDocuments(_uiState.value.selectedCategory, _uiState.value.searchQuery)
            }.onFailure { exception ->
                _uiState.update { it.copy(isUploading = false, errorMessage = exception.message) }
            }
        }
    }

    // ==========================================
    // 4. DELETE (HAPUS DOKUMEN)
    // ==========================================
    fun deleteDocument(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, errorMessage = null, successMessage = null) }

            val result = deleteKnowledgeDocumentUseCase(id)

            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isUploading = false,
                        successMessage = "Dokumen berhasil dihapus"
                    )
                }
                // Refresh list
                fetchDocuments(_uiState.value.selectedCategory, _uiState.value.searchQuery)
            }.onFailure { exception ->
                _uiState.update { it.copy(isUploading = false, errorMessage = exception.message) }
            }
        }
    }

    // ==========================================
    // FUNGSI INTERAKSI UI (Search, Tab, Clear Message)
    // ==========================================

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        fetchDocuments(category = category, search = _uiState.value.searchQuery)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        fetchDocuments(category = _uiState.value.selectedCategory, search = query)
    }

    fun clearMessage() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }
}