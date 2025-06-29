package com.task.autoeversecurity.component

import com.task.autoeversecurity.property.BasicAuthProperties
import com.task.autoeversecurity.repository.redis.BasicAuthUserRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class ApplicationInitializer(
    private val basicAuthUserRepository: BasicAuthUserRepository,
    private val basicAuthProperties: BasicAuthProperties,
) {
    @PostConstruct
    fun initialize() {
        // ADMIN 기본 권한을 Redis 에 저장하기 위해 initialize 메서드에서
        basicAuthUserRepository.setAdminInBasicAuthUsers(basicAuthProperties.admin.username, basicAuthProperties.admin.password)
        basicAuthUserRepository.addAllBasicAuthUsersInUserDetailsManager()
    }
}
