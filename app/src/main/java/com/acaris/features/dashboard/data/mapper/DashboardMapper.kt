package com.acaris.features.dashboard.data.mapper

import com.acaris.features.dashboard.data.remote.model.*
import com.acaris.features.dashboard.domain.model.*

fun DashboardMahasiswaResponse.toDomain(): DashboardMahasiswa {
    return DashboardMahasiswa(
        namaMahasiswa = this.namaMahasiswa.orEmpty(),
        npmMahasiswa = this.npmMahasiswa ?: "-",
        dosenPa = this.dosenPa ?: "-",
        nipDosen = this.nipDosen ?: "-",
        ipk = this.ipk ?: 0.0,
        semesterSaatIni = this.semesterSaatIni ?: 1,
        bimbinganSemesterIni = this.bimbinganSemesterIni ?: 0,
        bimbinganKeseluruhan = this.bimbinganKeseluruhan ?: 0,
        chatbotBulanIni = this.chatbotBulanIni ?: 0,
        jadwalTerdekat = this.jadwalTerdekat?.map { it.toDomain() } ?: emptyList(),
        kalenderBimbingan = this.kalenderBimbingan?.map { it.toDomain() } ?: emptyList(),
        fotoMahasiswa = this.fotoMahasiswa.orEmpty(),
        fotoDosen = this.fotoDosen.orEmpty(),
    )
}

fun JadwalSingkatResponse.toDomain(): JadwalSingkatDomain {
    return JadwalSingkatDomain(
        id = this.id.orEmpty(),
        date = this.date?.substringBefore("T").orEmpty(),
        startTime = this.startTime.orEmpty(),
        endTime = this.endTime.orEmpty(),
        status = this.status.orEmpty(),
        mahasiswaAgenda = this.mahasiswaAgenda.orEmpty(),
        keterangan = this.keterangan.orEmpty()
    )
}

fun DashboardDosenResponse.toDomain(): DashboardDosen {
    return DashboardDosen(
        namaDosen = this.namaDosen.orEmpty(),
        nipDosen = this.nipDosen ?: "-",
        fotoDosen = this.fotoDosen.orEmpty(),
        kodeKelas = this.kodeKelas ?: "-",
        jumlahMahasiswaBimbingan = this.jumlahMahasiswaBimbingan ?: 0,
        bimbinganHariIni = this.bimbinganHariIni ?: 0,
        bimbinganSemesterIni = this.bimbinganSemesterIni ?: 0,
        jadwalMingguIni = this.jadwalMingguIni?.map { it.toDomain() } ?: emptyList(),
        kalenderBimbingan = this.kalenderBimbingan?.map { it.toDomain() } ?: emptyList()
    )
}

fun JadwalMingguIniDosenResponse.toDomain(): JadwalMingguIniDosenDomain {
    return JadwalMingguIniDosenDomain(
        id = this.id.orEmpty(),
        date = this.date?.substringBefore("T").orEmpty(),
        startTime = this.startTime.orEmpty(),
        endTime = this.endTime.orEmpty(),
        status = this.status.orEmpty(),
        keterangan = this.keterangan.orEmpty(),
        listMahasiswa = this.mahasiswa?.map { it.toDomain() } ?: emptyList()
    )
}

fun MahasiswaBookingResponse.toDomain(): MahasiswaBookingDomain {
    return MahasiswaBookingDomain(
        nama = this.nama.orEmpty(),
        npm = this.npm.orEmpty(),
        agenda = this.agenda.orEmpty()
    )
}

// MAPPER BERSAMA (Dipakai Dosen & Mahasiswa)
fun KalenderItemResponse.toDomain(): KalenderItemDomain {
    return KalenderItemDomain(
        date = this.date?.substringBefore("T").orEmpty(),
        status = this.status.orEmpty()
    )
}