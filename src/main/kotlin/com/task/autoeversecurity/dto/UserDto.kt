package com.task.autoeversecurity.dto

import com.task.autoeversecurity.domain.entity.User

// TODO: spring validation 추가
data class UserJoinRequest(
    val loginId: String,
    val password: String,
    val name: String,
    val rrn: String,
    val phoneNumber: String,
    val address: AddressDto,
) {
    fun toEntity(encryptedPassword: String): User {
        return User(
            loginId = loginId,
            password = encryptedPassword,
            name = name,
            rrn = rrn,
            phoneNumber = phoneNumber,
            address = address.toEmbeddable(),
        )
    }
}

// TODO: spring validation 추가
data class UserLoginRequest(
    val loginId: String,
    val password: String,
)
