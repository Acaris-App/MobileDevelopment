package com.acaris.features.documents_mahasiswa.data.repository

import com.acaris.features.documents_mahasiswa.data.mapper.toDomain
import com.acaris.features.documents_mahasiswa.data.remote.datasource.DocumentApiService
import com.acaris.features.documents_mahasiswa.domain.model.Document
import com.acaris.features.documents_mahasiswa.domain.repository.DocumentRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class DocumentRepositoryImpl @Inject constructor(
    private val apiService: DocumentApiService
) : DocumentRepository {

    override suspend fun getDocuments(): Result<List<Document>> {
        return try {
            val response = apiService.getDocuments()
            if (response.status == "success" || response.status == "200") {

                val flatList = mutableListOf<Document>()
                val categorized = response.data?.documents

                categorized?.krs?.forEach { flatList.add(it.toDomain()) }
                categorized?.khs?.forEach { flatList.add(it.toDomain()) }
                categorized?.transkrip?.let { flatList.add(it.toDomain()) }

                Result.success(flatList)

            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadDocument(type: String, semester: Int?, file: File): Result<Document> {
        return try {
            val typeBody = type.toRequestBody("text/plain".toMediaTypeOrNull())
            val semesterBody = semester?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = apiService.uploadDocument(typeBody, semesterBody, filePart)

            val document = response.data?.toDomain() ?: throw Exception(response.message)
            Result.success(document)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDocument(documentId: String, semester: Int?, file: File?): Result<Document> {
        return try {
            val semesterBody = semester?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val filePart = file?.let {
                val requestFile = it.asRequestBody("application/pdf".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", it.name, requestFile)
            }

            val response = apiService.updateDocument(documentId, semesterBody, filePart)

            val document = response.data?.toDomain() ?: throw Exception(response.message)
            Result.success(document)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDocument(documentId: String): Result<Boolean> {
        return try {
            val response = apiService.deleteDocument(documentId)
            if (response.status == "success" || response.status == "200") {
                Result.success(true)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}