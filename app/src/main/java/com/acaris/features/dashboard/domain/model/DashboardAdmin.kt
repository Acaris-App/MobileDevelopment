package com.acaris.features.dashboard.domain.model

data class DashboardAdmin(
    val namaAdmin: String,
    val nipAdmin: String,
    val fotoAdmin: String,
    val totalMahasiswa: Int,
    val totalDosen: Int,
    val totalBimbingan: Int,
    val totalChatbot: Int,
    val topDosenBimbingan: List<TopDosenBimbinganDomain>,
    val topMahasiswaBimbingan: List<TopMahasiswaDomain>,
    val topMahasiswaChatbot: List<TopMahasiswaDomain>
)

data class TopDosenBimbinganDomain(
    val nama: String,
    val nip: String,
    val total: Int
)

// Digunakan bersama untuk Top Mahasiswa Bimbingan & Chatbot karena strukturnya sama
data class TopMahasiswaDomain(
    val nama: String,
    val npm: String,
    val total: Int
)