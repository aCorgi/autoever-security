package com.task.autoeversecurity.config

import com.task.autoeversecurity.repository.UserRepository
import com.task.autoeversecurity.repository.redis.BasicAuthAdminRepository
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class BasicAuthenticationProvider(
    private val passwordEncoder: PasswordEncoder,
    private val basicAuthAdminRepository: BasicAuthAdminRepository,
    private val userRepository: UserRepository,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val password = authentication.credentials.toString()

        basicAuthAdminRepository.getBasicAuthAdminsByNameOrNull(username)
            ?.let {
                if (passwordEncoder.matches(password, it)) {
                    val adminDetails =
                        AutoeverMember(
                            userId = 0,
                            loginId = username,
                            roles = listOf(AutoeverAuthority.ADMIN),
                        )

                    return UsernamePasswordAuthenticationToken(
                        adminDetails,
                        null,
                        adminDetails.roles,
                    )
                }
            }

        userRepository.findByLoginId(username)
            ?.let { user ->
                if (passwordEncoder.matches(password, user.password)) {
                    val userDetails =
                        AutoeverMember(
                            userId = user.id,
                            loginId = user.loginId,
                            roles = listOf(AutoeverAuthority.USER),
                        )

                    return UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.roles,
                    )
                }
            }

        throw BadCredentialsException("Invalid username or password")
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}

data class AutoeverMember(
    val userId: Int,
    val loginId: String,
    val roles: List<AutoeverAuthority>,
) : UserDetails {
    override fun getAuthorities() = roles

    override fun getPassword() = null

    override fun getUsername() = loginId

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}

enum class AutoeverAuthority : GrantedAuthority {
    ADMIN,
    USER,
    ;

    override fun getAuthority(): String {
        return name // "ADMIN", "USER"로 반환됨
    }
}
