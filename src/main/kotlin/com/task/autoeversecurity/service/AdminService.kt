package com.task.autoeversecurity.service

import com.task.autoeversecurity.dto.UserResponse
import com.task.autoeversecurity.dto.UserUpdateRequest
import com.task.autoeversecurity.util.AES256Encryptor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class AdminService(
    private val userService: UserService,
    private val aes256Encryptor: AES256Encryptor,
) {
    fun getPagedUsers(pageable: Pageable): Page<UserResponse> {
        return userService.getPagedUsers(pageable)
            .map { UserResponse(it) }
    }

    @Transactional
    fun updateUser(request: UserUpdateRequest) {
        val user = userService.findById(request.userId)

        request.password?.let {
            user.updatePassword(password = aes256Encryptor.encrypt(it))
        }

        request.address?.let {
            user.updateAddress(it)
        }
    }

    @Transactional
    fun deleteUser(userId: Int) {
        userService.deleteById(userId)
    }
}
