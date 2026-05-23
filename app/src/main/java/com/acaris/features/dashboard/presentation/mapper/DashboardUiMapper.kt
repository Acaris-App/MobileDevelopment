package com.acaris.features.dashboard.presentation.mapper

import com.acaris.core.utils.DateUtils
import com.acaris.features.dashboard.domain.model.*
import com.acaris.features.dashboard.presentation.model.*
import com.acaris.features.schedule.presentation.model.ScheduleStatus
import java.time.LocalDate
import java.util.Locale

// TRANSLATOR STATUS API KE ENUM (DASAR)
fun getRawMahasiswaStatus(dateString: String, apiStatus: String): ScheduleStatus {
    return try {
        val date = LocalDate.parse(dateString)
        if (date.isBefore(LocalDate.now())) return ScheduleStatus.SELESAI

        when (apiStatus.lowercase()) {
            "selesai" -> ScheduleStatus.SELESAI
            "booked", "booked_by_me" -> ScheduleStatus.BOOKED_BY_ME
            "full", "penuh" -> ScheduleStatus.FULL
            "available", "tersedia" -> ScheduleStatus.AVAILABLE
            else -> ScheduleStatus.NONE
        }
    } catch (e: Exception) { ScheduleStatus.NONE }
}

fun getRawDosenStatus(dateString: String, apiStatus: String): ScheduleStatus {
    return try {
        val date = LocalDate.parse(dateString)
        if (date.isBefore(LocalDate.now())) return ScheduleStatus.SELESAI

        when (apiStatus.lowercase()) {
            "selesai" -> ScheduleStatus.SELESAI
            "available", "tersedia" -> ScheduleStatus.AVAILABLE
            "full", "penuh", "booked" -> ScheduleStatus.FULL
            else -> ScheduleStatus.NONE
        }
    } catch (e: Exception) { ScheduleStatus.NONE }
}

// MAPPER MAHASISWA
fun DashboardMahasiswa.toPresentation(): DashboardMahasiswaUiModel {
    val mappedDates = mutableMapOf<LocalDate, ScheduleStatus>()

    this.kalenderBimbingan.forEach { item ->
        try {
            mappedDates[LocalDate.parse(item.date)] = getRawMahasiswaStatus(item.date, item.status)
        } catch (e: Exception) { /* Abaikan */ }
    }
    val filteredJadwalTerdekat = this.jadwalTerdekat
        .map { it.toPresentation() }
        .filter { it.status != ScheduleStatus.SELESAI }

    return DashboardMahasiswaUiModel(
        namaMahasiswa = this.namaMahasiswa,
        npmMahasiswa = this.npmMahasiswa,
        dosenPa = this.dosenPa,
        fotoMahasiswa = this.fotoMahasiswa,
        fotoDosen = this.fotoDosen,
        nipDosen = this.nipDosen,
        ipk = String.format(Locale.US, "%.2f", this.ipk),
        semesterSaatIni = this.semesterSaatIni.toString(),
        bimbinganSemesterIni = this.bimbinganSemesterIni.toString(),
        bimbinganKeseluruhan = this.bimbinganKeseluruhan.toString(),
        chatbotBulanIni = this.chatbotBulanIni.toString(),
        jadwalTerdekat = filteredJadwalTerdekat,
        kalenderBimbinganMap = mappedDates,
    )
}

fun JadwalSingkatDomain.toPresentation(): JadwalSingkatUiModel {
    return JadwalSingkatUiModel(
        id = this.id,
        date = DateUtils.formatShortDateToIndo(this.date),
        waktu = "${this.startTime} - ${this.endTime} WIB",
        agenda = this.mahasiswaAgenda,
        status = getRawMahasiswaStatus(this.date, this.status),
        keterangan = this.keterangan
    )
}

// MAPPER DOSEN
fun DashboardDosen.toPresentation(): DashboardDosenUiModel {
    val mappedDates = mutableMapOf<LocalDate, ScheduleStatus>()

    this.kalenderBimbingan.forEach { item ->
        try {
            mappedDates[LocalDate.parse(item.date)] = getRawDosenStatus(item.date, item.status)
        } catch (e: Exception) { /* Abaikan */ }
    }

    return DashboardDosenUiModel(
        namaDosen = this.namaDosen,
        nipDosen = this.nipDosen,
        fotoDosen = this.fotoDosen,
        kodeKelas = this.kodeKelas,
        jumlahMahasiswaBimbingan = this.jumlahMahasiswaBimbingan.toString(),
        bimbinganHariIni = this.bimbinganHariIni.toString(),
        bimbinganSemesterIni = this.bimbinganSemesterIni.toString(),
        jadwalMingguIni = this.jadwalMingguIni.map { it.toPresentation() },
        kalenderBimbinganMap = mappedDates
    )
}

fun JadwalMingguIniDosenDomain.toPresentation(): JadwalMingguIniUiModel {
    return JadwalMingguIniUiModel(
        id = this.id,
        date = DateUtils.formatShortDateToIndo(this.date),
        waktu = "${this.startTime} - ${this.endTime} WIB",
        status = getRawDosenStatus(this.date, this.status),
        keterangan = this.keterangan,
        listMahasiswa = this.listMahasiswa.map { it.toPresentation() }
    )
}

fun MahasiswaBookingDomain.toPresentation(): MahasiswaBookingUiModel {
    return MahasiswaBookingUiModel(
        nama = this.nama,
        npm = this.npm,
        agenda = this.agenda
    )
}