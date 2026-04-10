package com.acaris.features.documents_mahasiswa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.documents_mahasiswa.domain.usecase.DeleteDocumentUseCase
import com.acaris.features.documents_mahasiswa.domain.usecase.GetDocumentsUseCase
import com.acaris.features.documents_mahasiswa.domain.usecase.UploadDocumentUseCase
import com.acaris.features.documents_mahasiswa.presentation.model.DocumentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val deleteDocumentUseCase: DeleteDocumentUseCase,
    private val uploadDocumentUseCase: UploadDocumentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentState())
    val uiState: StateFlow<DocumentState> = _uiState.asStateFlow()

    fun loadDocuments() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = getDocumentsUseCase()
            result.fold(
                onSuccess = { list ->
                    _uiState.update { it.copy(isLoading = false, documents = list) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun uploadDocument(type: String, semester: Int?, file: File) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = uploadDocumentUseCase(type, semester, file)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    loadDocuments()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun deleteDocument(documentId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = deleteDocumentUseCase(documentId)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccessDelete = true) }
                    loadDocuments()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun resetDeleteState() {
        _uiState.update { it.copy(isSuccessDelete = false) }
    }
}