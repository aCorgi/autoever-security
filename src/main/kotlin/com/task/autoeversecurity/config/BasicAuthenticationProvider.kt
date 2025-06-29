package com.task.autoeversecurity.config

import com.task.autoeversecurity.repository.UserRepository
import com.task.autoeversecurity.repository.redis.BasicAuthUserRepository
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class BasicAuthenticationProvider(
    private val passwordEncoder: PasswordEncoder,
    private val basicAuthUserRepository: BasicAuthUserRepository,
    private val userRepository: UserRepository,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val password = authentication.credentials.toString()

        basicAuthUserRepository.getBasicAuthUsersByNameOrNull(username)
            ?.let {
                if (passwordEncoder.matches(password, it)) {
                    return UsernamePasswordAuthenticationToken(
                        username,
                        it,
                        emptyList(),
                    )
                }
            }

        userRepository.findByLoginId(username)
            ?.let { user ->
                if (passwordEncoder.matches(password, user.password)) {
                    return UsernamePasswordAuthenticationToken(
                        username,
                        user.password,
                        emptyList(),
                    )
                }
            }

        throw BadCredentialsException("Invalid username or password")
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}
