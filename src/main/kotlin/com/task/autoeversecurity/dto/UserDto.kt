package com.task.autoeversecurity.dto

import com.task.autoeversecurity.component.Aes256EncryptionManager
import com.task.autoeversecurity.domain.entity.User
import jakarta.validation.constraints.NotBlank

data class UserJoinRequest(
    @field:NotBlank
    val loginId: String,
    @field:NotBlank
    val password: String,
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val rrn: String,
    @field:NotBlank
    val phoneNumber: String,
    val address: AddressDto,
)

data class UserLoginRequest(
    @field:NotBlank
    val loginId: String,
    @field:NotBlank
    val password: String,
)

data class MyselfUserResponse(
    val id: Int,
    val loginId: String,
    val password: String,
    val name: String,
    val rrn: String,
    val age: Int,
    val phoneNumber: String,
    val city: String,
) {
    constructor(
        user: User,
        aes256EncryptionManager: Aes256EncryptionManager,
    ) : this(
        id = user.id,
        loginId = user.loginId,
        password = user.password,
        name = user.name,
        rrn = aes256EncryptionManager.decrypt(user.rrn),
        age = user.age,
        phoneNumber = aes256EncryptionManager.decrypt(user.phoneNumber),
        city = user.address.city,
    )
}
