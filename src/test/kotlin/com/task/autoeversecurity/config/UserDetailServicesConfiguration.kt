package com.task.autoeversecurity.config

import com.task.autoeversecurity.repository.redis.BasicAuthUserRepository
import com.task.autoeversecurity.util.TestConstants.BASIC_AUTH_PASSWORD
import com.task.autoeversecurity.util.TestConstants.BASIC_AUTH_USERNAME
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
class UserDetailServicesConfiguration(
    private val basicAuthUserRepository: BasicAuthUserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Bean
    fun createUserDetailService(): UserDetailsService {
        val userDetails =
            basicAuthUserRepository.getBasicAuthUsers()
                .map { (username, password) -> User(username, password, emptyList()) }

        return InMemoryUserDetailsManager(
            userDetails.plus(User(BASIC_AUTH_USERNAME, passwordEncoder.encode(BASIC_AUTH_PASSWORD), emptyList())),
        )
    }
}
