package com.acaris.features.documents_mahasiswa.data.remote.model

import com.google.gson.annotations.SerializedName

data class DocumentResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("document_type") val documentType: String?,
    @SerializedName("semester") val semester: Int?,
    @SerializedName("file_path") val filePath: String?,
    @SerializedName("uploaded_at") val uploadedAt: String?
)

data class DocumentListDataResponse(
    @SerializedName("total") val total: Int?,
    @SerializedName("documents") val documents: DocumentCategorizedResponse?
)

data class DocumentCategorizedResponse(
    @SerializedName("krs") val krs: List<DocumentResponse>?,
    @SerializedName("khs") val khs: List<DocumentResponse>?,
    @SerializedName("transkrip") val transkrip: DocumentResponse?
)