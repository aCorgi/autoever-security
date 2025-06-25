package com.task.autoeversecurity.util

import com.task.autoeversecurity.domain.entity.User

object MockUserEntity {
    fun of(
        loginId: String = "testUser",
        password: String = "password123",
        name: String = "Test User",
        rrn: String = "123456-7890123",
        phoneNumber: String = "01012345678",
        address: String = "123 Test Street, Test City, Test Country",
    ): User {
        return User(
            loginId = loginId,
            password = password,
            name = name,
            rrn = rrn,
            phoneNumber = phoneNumber,
            address = address,
        )
    }
}
