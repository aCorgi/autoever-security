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
)

// TODO: spring validation 추가
data class UserLoginRequest(
    val loginId: String,
    val password: String,
)

data class MyselfUserResponse(
    val id: Int,
    val loginId: String,
    val password: String,
    val name: String,
    val rrn: String,
    val phoneNumber: String,
    val city: String,
) {
    constructor(user: User) : this(
        id = user.id,
        loginId = user.loginId,
        password = user.password,
        name = user.name,
        rrn = user.rrn,
        phoneNumber = user.phoneNumber,
        city = user.address.city,
    )
}
