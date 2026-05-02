package com.acaris.features.knowledge_base.domain.usecase

import com.acaris.features.knowledge_base.domain.model.KnowledgeDocument
import com.acaris.features.knowledge_base.domain.repository.KnowledgeRepository
import java.io.File
import javax.inject.Inject

class UpdateKnowledgeDocumentUseCase @Inject constructor(
    private val repository: KnowledgeRepository
) {
    suspend operator fun invoke(id: String, title: String?, category: String?, file: File?): Result<KnowledgeDocument> {

        if (title != null && title.trim().isEmpty()) {
            return Result.failure(Exception("Judul dokumen tidak boleh kosong!"))
        }

        if (file != null) {
            if (file.extension.lowercase() != "pdf") {
                return Result.failure(Exception("Gagal! Dokumen pengganti harus berformat .pdf"))
            }

            val maxFileSize = 5L * 1024 * 1024
            if (file.length() > maxFileSize) {
                return Result.failure(Exception("Ukuran file pengganti terlalu besar! Maksimal 5 MB."))
            }
        }

        return repository.updateKnowledgeDocument(id, title, category, file)
    }
}