package com.task.autoeversecurity.service

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.task.autoeversecurity.dto.UserCreationRequest
import com.task.autoeversecurity.exception.ClientBadRequestException
import com.task.autoeversecurity.repository.UserRepository
import com.task.autoeversecurity.util.AES256Encryptor
import com.task.autoeversecurity.util.Constants.Exception.DUPLICATE_LOGIN_ID_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.DUPLICATE_RRN_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.IMPOSSIBLE_PHONE_NUMBER_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.INVALID_RRN_FORMAT_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.RRN_CONTAINS_HYPHEN_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.RRN_LENGTH_INVALID_EXCEPTION_MESSAGE
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class UserService(
    private val userRepository: UserRepository,
    private val aes256Encryptor: AES256Encryptor,
) {
    @Transactional
    fun join(request: UserCreationRequest) {
        validatePhoneNumber(request.phoneNumber)
        validateRrn(request.rrn)

        userRepository.findByLoginId(request.loginId)
            ?.let {
                throw ClientBadRequestException(DUPLICATE_LOGIN_ID_EXCEPTION_MESSAGE)
            }

        userRepository.findByRrn(request.rrn)
            ?.let {
                throw ClientBadRequestException(DUPLICATE_RRN_EXCEPTION_MESSAGE)
            }

        // 패스워드 암호화
        val encryptedPassword = aes256Encryptor.encrypt(request.password)

        // User 엔티티 생성 및 저장
        userRepository.save(request.toEntity(encryptedPassword))
    }

    private fun validatePhoneNumber(phoneNumber: String) {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()

        if (Regex("^01([0|1|6|7|8|9])(\\d{3,4})(\\d{4})$").matches(phoneNumber).not()) {
            throw ClientBadRequestException(IMPOSSIBLE_PHONE_NUMBER_EXCEPTION_MESSAGE)
        }

        val parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, "KR")
        if (phoneNumberUtil.isValidNumber(parsedPhoneNumber).not() ||
            (phoneNumberUtil.getNumberType(parsedPhoneNumber) != PhoneNumberUtil.PhoneNumberType.MOBILE)
        ) {
            throw ClientBadRequestException(IMPOSSIBLE_PHONE_NUMBER_EXCEPTION_MESSAGE)
        }
    }

    private fun validateRrn(rrn: String) {
        if (rrn.contains("-")) {
            throw ClientBadRequestException(RRN_CONTAINS_HYPHEN_EXCEPTION_MESSAGE)
        }

        if (rrn.length != 13) {
            throw ClientBadRequestException(RRN_LENGTH_INVALID_EXCEPTION_MESSAGE)
        }

        if (Regex("^[0-9]{6}[1-4][0-9]{6}\$").matches(rrn).not()) {
            throw ClientBadRequestException(INVALID_RRN_FORMAT_EXCEPTION_MESSAGE)
        }
    }
}
