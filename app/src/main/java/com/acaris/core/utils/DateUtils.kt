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
}