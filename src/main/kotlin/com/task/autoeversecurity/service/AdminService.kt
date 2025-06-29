package com.task.autoeversecurity.service

import com.task.autoeversecurity.dto.UserResponse
import com.task.autoeversecurity.dto.UserUpdateRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class AdminService(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
) {
    fun getPagedUsers(pageable: Pageable): Page<UserResponse> {
        return userService.getPagedUsers(pageable)
            .map { UserResponse(it) }
    }

    @Transactional
    fun updateUser(request: UserUpdateRequest) {
        val user = userService.findById(request.userId)

        request.password?.let {
            user.updatePassword(password = passwordEncoder.encode(it))
        }

        request.address?.let {
            user.updateAddress(it.toEmbeddable())
        }
    }

    @Transactional
    fun deleteUser(userId: Int) {
        userService.deleteById(userId)
    }
}
