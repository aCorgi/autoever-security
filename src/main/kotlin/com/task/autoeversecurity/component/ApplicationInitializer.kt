package com.task.autoeversecurity.component

import com.task.autoeversecurity.repository.redis.BasicAuthUserRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class ApplicationInitializer(
    private val basicAuthUserRepository: BasicAuthUserRepository,
) {
    @PostConstruct
    fun initialize() {
        basicAuthUserRepository.setAdminInBasicAuthUsers()
    }
}
