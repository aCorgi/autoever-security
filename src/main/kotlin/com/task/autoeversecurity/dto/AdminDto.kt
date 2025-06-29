package com.task.autoeversecurity.dto

import com.task.autoeversecurity.domain.entity.User

data class UserUpdateRequest(
    val userId: Int,
    val password: String?,
    val address: AddressDto?,
)

data class UserDeleteRequest(
    val userId: Int,
)

data class UserResponse(
    val id: Int,
    val loginId: String,
    val password: String,
    val name: String,
    val rrn: String,
    val phoneNumber: String,
    val address: AddressDto,
) {
    constructor(user: User) : this(
        id = user.id,
        loginId = user.loginId,
        password = user.password,
        name = user.name,
        rrn = user.rrn,
        phoneNumber = user.phoneNumber,
        address = AddressDto(user.address),
    )
}
