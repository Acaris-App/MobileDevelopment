package com.acaris.features.dashboard.data.remote.model

import com.google.gson.annotations.SerializedName

data class DashboardDosenResponse(
    @SerializedName("nama_dosen") val namaDosen: String?,
    @SerializedName("nip_dosen") val nipDosen: String?,
    @SerializedName("foto_dosen") val fotoDosen: String?,
    @SerializedName("kode_kelas") val kodeKelas: String?,
    @SerializedName("jumlah_mahasiswa_bimbingan") val jumlahMahasiswaBimbingan: Int?,
    @SerializedName("bimbingan_hari_ini") val bimbinganHariIni: Int?,
    @SerializedName("bimbingan_semester_ini") val bimbinganSemesterIni: Int?,
    @SerializedName("jadwal_minggu_ini") val jadwalMingguIni: List<JadwalMingguIniDosenResponse>?,
    @SerializedName("kalender_bimbingan") val kalenderBimbingan: List<KalenderItemResponse>?,
    @SerializedName("top_mahasiswa_bimbingan") val topMahasiswaBimbingan: List<TopMahasiswaResponse>?,
    @SerializedName("top_mahasiswa_chatbot") val topMahasiswaChatbot: List<TopMahasiswaResponse>?
)

data class JadwalMingguIniDosenResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("start_time") val startTime: String?,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("keterangan") val keterangan: String?,
    @SerializedName("mahasiswa") val mahasiswa: List<MahasiswaBookingResponse>?
)

data class MahasiswaBookingResponse(
    @SerializedName("nama") val nama: String?,
    @SerializedName("npm") val npm: String?,
    @SerializedName("agenda") val agenda: String?
)