package com.acaris.core.domain.usecase

import java.util.Calendar
import javax.inject.Inject

class CalculateSemesterUseCase @Inject constructor() {

    operator fun invoke(angkatanYearStr: String): String {
        val angkatanYear = angkatanYearStr.toIntOrNull() ?: return ""

        val calendar = Calendar.getInstance()
        val currYear = calendar.get(Calendar.YEAR)
        val currMonth = calendar.get(Calendar.MONTH) // 0-based: Jan=0, Aug=7

        val diffYear = currYear - angkatanYear

        val calculatedSemester = if (currMonth >= Calendar.AUGUST) {
            (diffYear * 2) + 1
        } else {
            (diffYear * 2)
        }

        // Aturan DO Kampus: Maksimal 14
        return when {
            calculatedSemester > 14 -> "14"
            calculatedSemester < 1 -> "1"
            else -> calculatedSemester.toString()
        }
    }
}