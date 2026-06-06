package com.acaris.core.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    /**
     * 🛡️ FUNGSI LAMA (JANGAN DIHAPUS)
     * Mengubah format ISO (2026-04-19T18:40:52.330Z)
     * Menjadi format Indonesia (Minggu, 19 April 2026)
     * Tetap dibiarkan agar fitur lain yang memanggil ini tidak error!
     */
    fun formatIsoToIndo(rawDate: String?): String {
        if (rawDate.isNullOrEmpty()) return "-"

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            outputFormat.timeZone = TimeZone.getDefault()

            val date = inputFormat.parse(rawDate)
            if (date != null) {
                outputFormat.format(date)
            } else {
                rawDate
            }
        } catch (e: Exception) {
            rawDate
        }
    }

    /**
     * 🌟 FUNGSI BARU (KHUSUS DASHBOARD)
     * Menangani tanggal yang sudah dipotong pendek (YYYY-MM-DD)
     * Contoh: "2026-05-18" menjadi "Senin, 18 Mei 2026"
     */
    fun formatShortDateToIndo(rawDate: String?): String {
        if (rawDate.isNullOrEmpty()) return "-"

        return try {
            val parsedLocalDate = LocalDate.parse(rawDate)
            parsedLocalDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("id", "ID")))
        } catch (e: Exception) {
            rawDate // Fallback jika format dari server aneh
        }
    }

    fun formatIsoToTimeOnly(rawDate: String?): String {
        if (rawDate.isNullOrEmpty() || rawDate == "Memuat...") return rawDate ?: ""

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            // Format output HANYA jam dan menit (24 jam)
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault() // Gunakan zona waktu HP user (WIB/WITA/WIT)

            val date = inputFormat.parse(rawDate)
            if (date != null) {
                outputFormat.format(date)
            } else {
                rawDate
            }
        } catch (e: Exception) {
            // Jika parsing ISO gagal (mungkin dari server datang string aneh), coba parsing standard
            try {
                // Fallback sederhana jika formatnya YYYY-MM-DD HH:mm:ss
                val fallbackFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = fallbackFormat.parse(rawDate)
                if (date != null) {
                    val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    outputFormat.format(date)
                } else rawDate
            } catch (ex: Exception) {
                rawDate
            }
        }
    }

    /**
     * 🌟 HELPER PENYELAMAT: Menerjemahkan UI date ("05 Jun 2026" atau "Senin, 05 Jun 2026")
     * kembali ke format API ("2026-06-05")
     */
    fun formatUiDateToApiDate(uiDate: String): String {
        if (uiDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) return uiDate
        try {
            val cleanDate = uiDate.replace(Regex("[a-zA-Z]+,"), "").trim()
            val parts = cleanDate.split(" ")
            if (parts.size >= 3) {
                val day = parts[0].padStart(2, '0')
                val monthStr = parts[1].lowercase()
                val month = when {
                    monthStr.startsWith("jan") -> "01"
                    monthStr.startsWith("feb") -> "02"
                    monthStr.startsWith("mar") -> "03"
                    monthStr.startsWith("apr") -> "04"
                    monthStr.startsWith("mei") -> "05"
                    monthStr.startsWith("jun") -> "06"
                    monthStr.startsWith("jul") -> "07"
                    monthStr.startsWith("agu") || monthStr.startsWith("ags") -> "08"
                    monthStr.startsWith("sep") -> "09"
                    monthStr.startsWith("okt") -> "10"
                    monthStr.startsWith("nov") -> "11"
                    monthStr.startsWith("des") -> "12"
                    else -> "01"
                }
                val year = parts[2]
                return "$year-$month-$day"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return LocalDate.now().toString()
    }
}