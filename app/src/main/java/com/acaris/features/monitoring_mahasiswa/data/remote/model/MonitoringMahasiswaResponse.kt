package com.acaris.features.monitoring_mahasiswa.data.remote.model

import com.google.gson.annotations.SerializedName

data class MahasiswaBimbinganResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("npm_nip") val npmNip: String?,
    @SerializedName("profile_picture") val profilePicture: String?,
    @SerializedName("angkatan") val angkatan: Int?,
    @SerializedName("current_semester") val currentSemester: Int?
)

data class DokumenBimbinganResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("document_type") val documentType: String?,
    @SerializedName("semester") val semester: Int?,
    @SerializedName("file_path") val filePath: String?,
    @SerializedName("uploaded_at") val uploadedAt: String?
)

data class DetailMahasiswaResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("npm_nip") val npmNip: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("profile_picture") val profilePicture: String?,
    @SerializedName("angkatan") val angkatan: Int?,
    @SerializedName("ipk") val ipk: String?,
    @SerializedName("current_semester") val currentSemester: Int?,
    @SerializedName("kode_kelas") val kodeKelas: String?,
    @SerializedName("documents") val documents: List<DokumenBimbinganResponse>?
)

data class RiwayatBimbinganResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("time") val time: String?,
    @SerializedName("agenda") val agenda: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("keterangan") val keteranganDosen: String?
)