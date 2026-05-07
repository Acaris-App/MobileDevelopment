// File: core/utils/DateUtils.kt
package com.acaris.core.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    /**
     * Mengubah format ISO (2026-04-19T18:40:52.330Z)
     * Menjadi format Indonesia (Minggu, 19 April 2026)
     */
    fun formatIsoToIndo(rawDate: String?): String {
        if (rawDate.isNullOrEmpty()) return "-" // Jaga-jaga kalau data dari server null

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
            rawDate // Fallback jika format dari peladen tidak sesuai
        }
    }
}