package com.task.autoeversecurity.config

import com.task.autoeversecurity.repository.redis.BasicAuthUserRepository
import com.task.autoeversecurity.util.TestConstants.BASIC_AUTH_PASSWORD
import com.task.autoeversecurity.util.TestConstants.BASIC_AUTH_USERNAME
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.test.context.bean.override.mockito.MockitoBean

class UserDetailServicesConfigurationIT : IntegrationTestBase() {
    @MockitoBean
    private lateinit var basicAuthUserRepository: BasicAuthUserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Test
    fun `userDetailsService 에서 불러온 정보 기반으로 암호 인증에 성공한다`() {
        whenever(basicAuthUserRepository.getBasicAuthUsers())
            .thenReturn(mapOf(BASIC_AUTH_USERNAME to passwordEncoder.encode(BASIC_AUTH_PASSWORD)))

        InMemoryUserDetailsManager(User(BASIC_AUTH_USERNAME, passwordEncoder.encode(BASIC_AUTH_PASSWORD), emptyList()))

        val user = userDetailsService.loadUserByUsername(BASIC_AUTH_USERNAME)

        assertThat(user).isNotNull
        assertThat(user.username).isEqualTo(BASIC_AUTH_USERNAME)
        assertThat(passwordEncoder.matches(BASIC_AUTH_PASSWORD, user.password)).isTrue
    }
}
