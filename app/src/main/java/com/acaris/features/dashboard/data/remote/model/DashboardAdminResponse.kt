package com.acaris.features.dashboard.data.remote.model

import com.google.gson.annotations.SerializedName

data class DashboardAdminResponse(
    @SerializedName("nama_admin") val namaAdmin: String?,
    @SerializedName("nip_admin") val nipAdmin: String?,
    @SerializedName("foto_admin") val fotoAdmin: String?,
    @SerializedName("total_mahasiswa") val totalMahasiswa: Int?,
    @SerializedName("total_dosen") val totalDosen: Int?,
    @SerializedName("total_bimbingan") val totalBimbingan: Int?,
    @SerializedName("total_chatbot") val totalChatbot: Int?,
    @SerializedName("top_dosen_bimbingan") val topDosenBimbingan: List<TopDosenResponse>?,
    @SerializedName("top_mahasiswa_bimbingan") val topMahasiswaBimbingan: List<TopMahasiswaResponse>?,
    @SerializedName("top_mahasiswa_chatbot") val topMahasiswaChatbot: List<TopMahasiswaResponse>?
)

data class TopDosenResponse(
    @SerializedName("nama") val nama: String?,
    @SerializedName("nip") val nip: String?,
    @SerializedName("total") val total: Int?
)

data class TopMahasiswaResponse(
    @SerializedName("nama") val nama: String?,
    @SerializedName("npm") val npm: String?,
    @SerializedName("total") val total: Int?
)