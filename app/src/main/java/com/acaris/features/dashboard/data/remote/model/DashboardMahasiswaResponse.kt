package com.acaris.features.dashboard.data.remote.model

import com.google.gson.annotations.SerializedName

data class DashboardMahasiswaResponse(
    @SerializedName("nama_mahasiswa") val namaMahasiswa: String?,
    @SerializedName("npm_mahasiswa") val npmMahasiswa: String?,
    @SerializedName("dosen_pa") val dosenPa: String?,
    @SerializedName("ipk") val ipk: Double?,
    @SerializedName("semester_saat_ini") val semesterSaatIni: Int?,
    @SerializedName("bimbingan_semester_ini") val bimbinganSemesterIni: Int?,
    @SerializedName("bimbingan_keseluruhan") val bimbinganKeseluruhan: Int?,
    @SerializedName("chatbot_bulan_ini") val chatbotBulanIni: Int?,
    @SerializedName("jadwal_terdekat") val jadwalTerdekat: List<JadwalSingkatResponse>?,
    @SerializedName("kalender_bimbingan") val kalenderBimbingan: List<KalenderItemResponse>?,
    @SerializedName("foto_mahasiswa") val fotoMahasiswa: String?,
    @SerializedName("foto_dosen") val fotoDosen: String?,
    @SerializedName("nip_dosen") val nipDosen: String?,
)

data class JadwalSingkatResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("start_time") val startTime: String?,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("mahasiswa_agenda") val mahasiswaAgenda: String?,
    @SerializedName("keterangan") val keterangan: String
)

data class KalenderItemResponse(
    @SerializedName("date") val date: String?,
    @SerializedName("status") val status: String?
)