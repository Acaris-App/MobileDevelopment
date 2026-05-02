package com.acaris.features.knowledge_base.data.remote.model

import com.google.gson.annotations.SerializedName

data class KnowledgeDocumentResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("file_name") val fileName: String?,
    @SerializedName("file_url") val fileUrl: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("uploaded_at") val uploadedAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)