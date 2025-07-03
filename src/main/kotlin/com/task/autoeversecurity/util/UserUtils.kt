package com.task.autoeversecurity.util

import com.task.autoeversecurity.exception.ClientBadRequestException
import java.time.LocalDate

object UserUtils {
    fun getAgeFromRrn(rrn: String): Int {
        val birthYearTwoDigits = rrn.substring(0, 2).toInt()
        val genderCode = rrn[6]

        val currentYear = LocalDate.now().year

        val fullBirthYear =
            when (genderCode) {
                '1', '2' -> 1900 + birthYearTwoDigits
                '3', '4' -> 2000 + birthYearTwoDigits
                else -> throw ClientBadRequestException("Invalid gender code in RRN: $genderCode")
            }

        return currentYear - fullBirthYear
    }
}
