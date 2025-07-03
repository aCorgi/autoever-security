package com.task.autoeversecurity.service

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.task.autoeversecurity.component.Aes256EncryptionManager
import com.task.autoeversecurity.config.AutoeverMember
import com.task.autoeversecurity.domain.entity.User
import com.task.autoeversecurity.dto.AgeGroup
import com.task.autoeversecurity.dto.MyselfUserResponse
import com.task.autoeversecurity.dto.UserJoinRequest
import com.task.autoeversecurity.dto.UserLoginRequest
import com.task.autoeversecurity.exception.ClientBadRequestException
import com.task.autoeversecurity.exception.ResourceNotFoundException
import com.task.autoeversecurity.repository.UserRepository
import com.task.autoeversecurity.util.CommonUtils.getBasicAuthToken
import com.task.autoeversecurity.util.Constants.Exception.DUPLICATE_LOGIN_ID_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.DUPLICATE_RRN_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.IMPOSSIBLE_PHONE_NUMBER_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.INVALID_RRN_FORMAT_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.PASSWORD_MISMATCH_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.RRN_CONTAINS_HYPHEN_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.RRN_LENGTH_INVALID_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.USER_NOT_FOUND_EXCEPTION_MESSAGE
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val aes256EncryptionManager: Aes256EncryptionManager,
) {
    fun findByAgeGroup(ageGroup: AgeGroup): List<User> {
        return userRepository.findByAgeBetween(
            minAge = ageGroup.minAge,
            maxAge = ageGroup.maxAge,
        )
    }

    fun getMyself(autoeverMember: AutoeverMember): MyselfUserResponse {
        val user = findById(autoeverMember.userId)

        return MyselfUserResponse(user, aes256EncryptionManager)
    }

    @Transactional
    fun join(request: UserJoinRequest) {
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

        val userToCreate =
            User(
                request = request,
                passwordEncoder = passwordEncoder,
                aes256EncryptionManager = aes256EncryptionManager,
            )

        // User 엔티티 생성 및 저장
        userRepository.save(userToCreate)
    }

    fun findById(id: Int): User {
        return userRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE)
    }

    fun findByLoginId(loginId: String): User {
        return userRepository.findByLoginId(loginId)
            ?: throw ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE)
    }

    @Transactional
    fun deleteById(id: Int) {
        findById(id)

        userRepository.deleteById(id)
    }

    fun login(request: UserLoginRequest): String {
        val user = findByLoginId(request.loginId)

        if (passwordEncoder.matches(request.password, user.password).not()) {
            throw ClientBadRequestException(PASSWORD_MISMATCH_EXCEPTION_MESSAGE)
        }

        return getBasicAuthToken(request.loginId, request.password)
    }

    fun getPagedUsers(pageable: Pageable): Page<User> {
        return userRepository.findAll(pageable)
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
