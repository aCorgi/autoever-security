package com.task.autoeversecurity.service

import com.task.autoeversecurity.config.UnitTestBase
import com.task.autoeversecurity.domain.entity.User
import com.task.autoeversecurity.dto.UserJoinRequest
import com.task.autoeversecurity.dto.UserLoginRequest
import com.task.autoeversecurity.exception.ClientBadRequestException
import com.task.autoeversecurity.exception.ResourceNotFoundException
import com.task.autoeversecurity.repository.UserRepository
import com.task.autoeversecurity.util.Constants.Exception.DUPLICATE_LOGIN_ID_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.DUPLICATE_RRN_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.PASSWORD_MISMATCH_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.USER_NOT_FOUND_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.MockUserEntity
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals

class UserServiceTest : UnitTestBase() {
    @InjectMocks
    private lateinit var userService: UserService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Nested
    inner class `회원가입` {
        @Nested
        inner class `성공` {
            @Test
            fun `중복이 없는 유효한 요청 값임을 검증하고 User 데이터를 create 한다`() {
                // given
                val loginId = "validUser"
                val password = "securePassword"
                val name = "John Doe"
                val rrn = "9101011234924"
                val phoneNumber = "01012345678"
                val address = "Seoul, Korea"
                val userJoinRequest =
                    UserJoinRequest(
                        loginId = loginId,
                        password = password,
                        name = name,
                        rrn = rrn,
                        phoneNumber = phoneNumber,
                        address = address,
                    )

                whenever(userRepository.findByLoginId(loginId))
                    .thenReturn(null)
                whenever(userRepository.findByRrn(rrn))
                    .thenReturn(null)
                whenever(passwordEncoder.encode(userJoinRequest.password))
                    .thenReturn("encryptedPassword")
                whenever(userRepository.save(Mockito.any()))
                    .thenReturn(userJoinRequest.toEntity("encryptedPassword"))

                // when & then
                assertDoesNotThrow {
                    userService.join(userJoinRequest)
                }
            }
        }

        @Nested
        inner class `실패` {
            @Test
            fun `이미 존재하는 로그인 ID 가 있으면`() {
                // given
                val loginId = "duplicateUser"
                val password = "securePassword"
                val name = "John Doe"
                val rrn = "9101011234924"
                val phoneNumber = "01012345678"
                val address = "Seoul, Korea"
                val userJoinRequest =
                    UserJoinRequest(
                        loginId = loginId,
                        password = password,
                        name = name,
                        rrn = rrn,
                        phoneNumber = phoneNumber,
                        address = address,
                    )
                val encryptedPassword = "encryptedPassword"

                whenever(userRepository.findByLoginId(loginId))
                    .thenReturn(userJoinRequest.toEntity(encryptedPassword))

                // when & then
                assertThrows<ClientBadRequestException> {
                    userService.join(userJoinRequest)
                }
                    .also { assertEquals(it.message, DUPLICATE_LOGIN_ID_EXCEPTION_MESSAGE) }
            }

            @Test
            fun `이미 동일한 주민등록번호로 저장되어 있으면`() {
                // given
                val loginId = "validUser"
                val password = "securePassword"
                val name = "John Doe"
                val rrn = "9101011234924"
                val phoneNumber = "01012345678"
                val address = "Seoul, Korea"
                val userJoinRequest =
                    UserJoinRequest(
                        loginId = loginId,
                        password = password,
                        name = name,
                        rrn = rrn,
                        phoneNumber = phoneNumber,
                        address = address,
                    )
                val encryptedPassword = "encryptedPassword"

                whenever(userRepository.findByLoginId(loginId))
                    .thenReturn(null)
                whenever(userRepository.findByRrn(rrn))
                    .thenReturn(userJoinRequest.toEntity(encryptedPassword))

                // when & then
                assertThrows<ClientBadRequestException> {
                    userService.join(userJoinRequest)
                }
                    .also { assertEquals(it.message, DUPLICATE_RRN_EXCEPTION_MESSAGE) }
            }
        }
    }

    @Nested
    inner class `로그인` {
        @Nested
        inner class `성공` {
            @Test
            fun `유효한 로그인 ID와 비밀번호로 로그인에 성공한다`() {
                // given
                val loginId = "validUser"
                val password = "securePassword"
                val encryptedPassword = "encryptedPassword"
                val user = MockUserEntity.of(password = encryptedPassword)

                whenever(userRepository.findByLoginId(loginId))
                    .thenReturn(user)
                whenever(passwordEncoder.matches(password, encryptedPassword))
                    .thenReturn(true)

                // when
                val token = userService.login(UserLoginRequest(loginId, password))

                // then
                assertEquals("Basic ${Base64.getEncoder().encodeToString("$loginId:$password".toByteArray())}", token)
            }
        }

        @Nested
        inner class `실패` {
            @Test
            fun `존재하지 않는 로그인 ID로 로그인 시도 시 예외를 던진다`() {
                // given
                val loginId = "nonExistentUser"
                val password = "securePassword"

                whenever(userRepository.findByLoginId(loginId))
                    .thenReturn(null)

                // when & then
                assertThrows<ResourceNotFoundException> {
                    userService.login(UserLoginRequest(loginId, password))
                }
                    .also { assertEquals(it.message, USER_NOT_FOUND_EXCEPTION_MESSAGE) }
            }

            @Test
            fun `잘못된 비밀번호로 로그인 시도 시 예외를 던진다`() {
                // given
                val loginId = "validUser"
                val password = "wrongPassword"
                val encryptedPassword = "encryptedPassword"
                val user =
                    User(
                        loginId = loginId,
                        password = encryptedPassword,
                        name = "John Doe",
                        rrn = "9101011234924",
                        phoneNumber = "01012345678",
                        address = "Seoul, Korea",
                    )

                whenever(userRepository.findByLoginId(loginId))
                    .thenReturn(user)
                whenever(passwordEncoder.matches(password, encryptedPassword))
                    .thenReturn(false)

                // when & then
                assertThrows<ClientBadRequestException> {
                    userService.login(UserLoginRequest(loginId, password))
                }
                    .also { assertEquals(it.message, PASSWORD_MISMATCH_EXCEPTION_MESSAGE) }
            }
        }
    }
}
