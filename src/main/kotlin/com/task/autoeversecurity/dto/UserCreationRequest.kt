package com.task.autoeversecurity.dto

import com.task.autoeversecurity.domain.entity.User

data class UserCreationRequest(
    val loginId: String,
    val password: String,
    val name: String,
    val rrn: String,
    val phoneNumber: String,
    val address: String,
) {
    fun toEntity(encryptedPassword: String): User {
        return User(
            loginId = loginId,
            password = encryptedPassword,
            name = name,
            rrn = rrn,
            phoneNumber = phoneNumber,
            address = address,
        )
    }
}
