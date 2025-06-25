package com.task.autoeversecurity.repository

import com.task.autoeversecurity.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Int> {
    fun findByLoginId(loginId: String): User?

    fun findByRrn(rrn: String): User?
}
