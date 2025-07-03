package com.task.autoeversecurity.dto

import com.task.autoeversecurity.domain.entity.User
import jakarta.validation.constraints.Min

data class UserUpdateRequest(
    @field:Min(1)
    val userId: Int,
    val password: String?,
    val address: AddressDto?,
)

data class UserDeleteRequest(
    @field:Min(1)
    val userId: Int,
)

data class UserResponse(
    val id: Int,
    val loginId: String,
    val name: String,
    val phoneNumber: String,
    val address: AddressDto,
) {
    constructor(user: User) : this(
        id = user.id,
        loginId = user.loginId,
        name = user.name,
        phoneNumber = user.phoneNumber,
        address = AddressDto(user.address),
    )
}
