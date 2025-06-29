package com.task.autoeversecurity.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
class UserDetailServicesConfiguration {
    @Bean
    fun inMemoryUserDetailsManager(): InMemoryUserDetailsManager {
        return InMemoryUserDetailsManager()
    }
}
