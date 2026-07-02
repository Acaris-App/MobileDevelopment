package com.acaris.features.knowledge_base.domain.usecase

import com.acaris.features.knowledge_base.domain.model.KnowledgeDocument
import com.acaris.features.knowledge_base.domain.repository.KnowledgeRepository
import java.io.File
import javax.inject.Inject

class UploadKnowledgeDocumentUseCase @Inject constructor(
    private val repository: KnowledgeRepository
) {
    suspend operator fun invoke(title: String, category: String, file: File): Result<KnowledgeDocument> {

        if (title.trim().isEmpty()) {
            return Result.failure(Exception("Judul dokumen tidak boleh kosong!"))
        }

        if (file.extension.lowercase() != "pdf") {
            return Result.failure(Exception("Gagal! AI Aca hanya bisa membaca file berformat .pdf"))
        }

        val maxFileSize = 50L * 1024 * 1024
        if (file.length() > maxFileSize) {
            return Result.failure(Exception("Ukuran file terlalu besar! Maksimal 50 MB."))
        }

        return repository.uploadKnowledgeDocument(title, category, file)
    }
}