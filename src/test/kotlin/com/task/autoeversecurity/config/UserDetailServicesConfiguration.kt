package com.task.autoeversecurity.config

import com.task.autoeversecurity.util.TestConstants.BASIC_AUTH_PASSWORD
import com.task.autoeversecurity.util.TestConstants.BASIC_AUTH_USERNAME
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
class UserDetailServicesConfiguration(
    private val passwordEncoder: PasswordEncoder,
) {
    @Bean
    fun inMemoryUserDetailsManager(): InMemoryUserDetailsManager {
        return InMemoryUserDetailsManager(User(BASIC_AUTH_USERNAME, passwordEncoder.encode(BASIC_AUTH_PASSWORD), emptyList()))
    }
}
