package com.task.autoeversecurity.component

import com.task.autoeversecurity.repository.redis.BasicAuthUserRepository
import com.task.autoeversecurity.util.Constants.BASIC_AUTH_ADMIN_NAME
import com.task.autoeversecurity.util.Constants.BASIC_AUTH_ADMIN_PASSWORD
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class ApplicationInitializer(
    private val basicAuthUserRepository: BasicAuthUserRepository,
) {
    @PostConstruct
    fun initialize() {
        // ADMIN 기본 권한을 Redis 에 저장하기 위해 initialize 메서드에서
        basicAuthUserRepository.setAdminInBasicAuthUsers(BASIC_AUTH_ADMIN_NAME, BASIC_AUTH_ADMIN_PASSWORD)
        basicAuthUserRepository.addAllBasicAuthUsersInUserDetailsManager()
    }
}
