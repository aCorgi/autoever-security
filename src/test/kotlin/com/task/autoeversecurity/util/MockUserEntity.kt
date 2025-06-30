package com.task.autoeversecurity.util

import com.task.autoeversecurity.domain.embeddable.Address
import com.task.autoeversecurity.domain.entity.User

object MockUserEntity {
    fun of(
        loginId: String = "testUser",
        password: String = "password123",
        name: String = "Test User",
        rrn: String = "123456-7890123",
        phoneNumber: String = "01012345678",
        age: Int = 15,
        address: Address =
            Address(
                city = "서울시",
                district = "관악구",
                town = "청룡 7길 50",
            ),
    ): User {
        return User(
            loginId = loginId,
            password = password,
            name = name,
            rrn = rrn,
            phoneNumber = phoneNumber,
            age = age,
            address = address,
        )
    }
}
