package com.acaris.features.knowledge_base.data.repository

import com.acaris.features.knowledge_base.data.mapper.toDomain
import com.acaris.features.knowledge_base.data.remote.datasource.KnowledgeApiService
import com.acaris.features.knowledge_base.domain.model.KnowledgeDocument
import com.acaris.features.knowledge_base.domain.repository.KnowledgeRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class KnowledgeRepositoryImpl @Inject constructor(
    private val apiService: KnowledgeApiService
) : KnowledgeRepository {

    override suspend fun getKnowledgeDocuments(category: String?, search: String?): Result<List<KnowledgeDocument>> {
        return try {
            val response = apiService.getKnowledgeDocuments(category, search)
            if (response.status == "success" || response.status == "200") {
                val list = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(list)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengambil data"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengambil data dokumen. Periksa koneksi Anda."))
        }
    }

    override suspend fun uploadKnowledgeDocument(title: String, category: String, file: File): Result<KnowledgeDocument> {
        return try {
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())

            val fileBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, fileBody)

            val response = apiService.uploadKnowledgeDocument(titleBody, categoryBody, filePart)

            if (response.status == "success" || response.status == "200") {
                val doc = response.data?.toDomain() ?: throw Exception("Data kosong")
                Result.success(doc)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengunggah dokumen"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengunggah dokumen. Periksa koneksi Anda."))
        }
    }

    override suspend fun updateKnowledgeDocument(id: String, title: String?, category: String?, file: File?): Result<KnowledgeDocument> {
        return try {
            val titleBody = title?.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryBody = category?.toRequestBody("text/plain".toMediaTypeOrNull())

            val filePart = file?.let {
                val fileBody = it.asRequestBody("application/pdf".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", it.name, fileBody)
            }

            val response = apiService.updateKnowledgeDocument(id, titleBody, categoryBody, filePart)

            if (response.status == "success" || response.status == "200") {
                val doc = response.data?.toDomain() ?: throw Exception("Data kosong")
                Result.success(doc)
            } else {
                Result.failure(Exception(response.message ?: "Gagal memperbarui dokumen"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal memperbarui dokumen. Periksa koneksi Anda."))
        }
    }

    override suspend fun deleteKnowledgeDocument(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteKnowledgeDocument(id)
            if (response.status == "success" || response.status == "200") {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Gagal menghapus dokumen"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal menghapus dokumen. Periksa koneksi Anda."))
        }
    }
}