package com.task.autoeversecurity.service

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
import com.task.autoeversecurity.util.Constants.Exception.PASSWORD_MISMATCH_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.USER_NOT_FOUND_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.UserUtils.validatePhoneNumber
import com.task.autoeversecurity.util.UserUtils.validateRrn
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

        return MyselfUserResponse(
            user = user,
            decryptedRrn = aes256EncryptionManager.decrypt(user.rrn),
            decryptedPhoneNumber = aes256EncryptionManager.decrypt(user.phoneNumber),
        )
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
                encryptedPassword = passwordEncoder.encode(request.password),
                encryptedRrn = aes256EncryptionManager.encrypt(request.rrn),
                encryptedPhoneNumber = aes256EncryptionManager.encrypt(request.phoneNumber),
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
}
