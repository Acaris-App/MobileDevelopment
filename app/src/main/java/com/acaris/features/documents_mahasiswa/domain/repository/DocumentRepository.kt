package com.acaris.features.documents_mahasiswa.domain.repository

import com.acaris.features.documents_mahasiswa.domain.model.Document
import java.io.File

interface DocumentRepository {
    suspend fun getDocuments(): Result<List<Document>>
    suspend fun uploadDocument(type: String, semester: Int?, file: File): Result<Document>
    suspend fun updateDocument(documentId: String, semester: Int?, file: File?): Result<Document>
    suspend fun deleteDocument(documentId: String): Result<Boolean>
}