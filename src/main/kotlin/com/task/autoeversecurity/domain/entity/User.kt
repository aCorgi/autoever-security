package com.task.autoeversecurity.domain.entity

import com.task.autoeversecurity.domain.BaseEntity
import com.task.autoeversecurity.domain.embeddable.Address
import com.task.autoeversecurity.dto.UserJoinRequest
import com.task.autoeversecurity.util.UserUtils.getAgeFromRrn
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    indexes = [
        Index(name = "user_uk01", columnList = "loginId", unique = true),
        Index(name = "user_uk02", columnList = "rrn", unique = true),
    ],
)
class User(
    @Column(nullable = false, length = 100)
    val loginId: String,
    password: String,
    @Column(nullable = false, length = 100)
    val name: String,
    // 주민등록번호
    @Column(nullable = false, length = 500)
    val rrn: String,
    age: Int,
    phoneNumber: String,
    address: Address,
) : BaseEntity() {
    constructor(
        request: UserJoinRequest,
        encryptedPassword: String,
        encryptedRrn: String,
        encryptedPhoneNumber: String,
    ) : this(
        loginId = request.loginId,
        password = encryptedPassword,
        name = request.name,
        rrn = encryptedRrn,
        age = getAgeFromRrn(request.rrn),
        phoneNumber = encryptedPhoneNumber,
        address = request.address.toEmbeddable(),
    )

    @Column(nullable = false, length = 500)
    var password: String = password
        protected set

    @Column(nullable = false, length = 500)
    var phoneNumber: String = phoneNumber
        protected set

    @Column(nullable = false)
    var age: Int = age
        protected set

    @Embedded
    var address: Address = address
        protected set

    fun updatePassword(password: String) {
        this.password = password
    }

    fun updateAddress(address: Address) {
        this.address = address
    }
}
