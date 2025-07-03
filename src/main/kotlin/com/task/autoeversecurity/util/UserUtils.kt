package com.task.autoeversecurity.util

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.task.autoeversecurity.exception.ClientBadRequestException
import java.time.LocalDate

object UserUtils {
    fun validatePhoneNumber(phoneNumber: String) {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()

        if (Regex("^01([0|1|6|7|8|9])(\\d{3,4})(\\d{4})$").matches(phoneNumber).not()) {
            throw ClientBadRequestException(Constants.Exception.IMPOSSIBLE_PHONE_NUMBER_EXCEPTION_MESSAGE)
        }

        val parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, "KR")
        if (phoneNumberUtil.isValidNumber(parsedPhoneNumber).not() ||
            (phoneNumberUtil.getNumberType(parsedPhoneNumber) != PhoneNumberUtil.PhoneNumberType.MOBILE)
        ) {
            throw ClientBadRequestException(Constants.Exception.IMPOSSIBLE_PHONE_NUMBER_EXCEPTION_MESSAGE)
        }
    }

    fun validateRrn(rrn: String) {
        if (rrn.contains("-")) {
            throw ClientBadRequestException(Constants.Exception.RRN_CONTAINS_HYPHEN_EXCEPTION_MESSAGE)
        }

        if (rrn.length != 13) {
            throw ClientBadRequestException(Constants.Exception.RRN_LENGTH_INVALID_EXCEPTION_MESSAGE)
        }

        if (Regex("^[0-9]{6}[1-4][0-9]{6}\$").matches(rrn).not()) {
            throw ClientBadRequestException(Constants.Exception.INVALID_RRN_FORMAT_EXCEPTION_MESSAGE)
        }
    }

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
