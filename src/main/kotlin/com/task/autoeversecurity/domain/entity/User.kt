package com.task.autoeversecurity.domain.entity

import com.task.autoeversecurity.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

// 계정/암호/성명/주민등록번호/핸드폰번호/주소 입니다.
// 핸드폰번호, 주민등록번호는 11자리 등의 자릿수... 구
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
    @Column(nullable = false, length = 50)
    val rrn: String,
    phoneNumber: String,
    address: String,
) : BaseEntity() {
    @Column(nullable = false, length = 500)
    var password: String = password
        protected set

    @Column(nullable = false, length = 20)
    var phoneNumber: String = phoneNumber
        protected set

    @Column(nullable = false, length = 200)
    var address: String = address
        protected set
}
