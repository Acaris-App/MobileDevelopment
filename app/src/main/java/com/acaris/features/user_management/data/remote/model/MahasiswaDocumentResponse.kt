package com.acaris.features.user_management.data.remote.model

import com.google.gson.annotations.SerializedName

data class MahasiswaDocumentResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("document_type") val documentType: String?,
    @SerializedName("semester") val semester: Int?,
    @SerializedName("file_path") val filePath: String?,
    @SerializedName("uploaded_at") val uploadedAt: String?
)