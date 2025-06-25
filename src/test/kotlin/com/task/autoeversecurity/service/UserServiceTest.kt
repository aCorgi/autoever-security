package com.task.autoeversecurity.service

import com.task.autoeversecurity.config.UnitTestBase
import com.task.autoeversecurity.dto.UserCreationRequest
import com.task.autoeversecurity.exception.ClientBadRequestException
import com.task.autoeversecurity.repository.UserRepository
import com.task.autoeversecurity.util.Constants.Exception.DUPLICATE_LOGIN_ID_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.DUPLICATE_RRN_EXCEPTION_MESSAGE
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class UserServiceTest : UnitTestBase() {
    @InjectMocks
    private lateinit var userService: UserService

    @Mock
    private lateinit var userRepository: UserRepository

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
                val userCreationRequest =
                    UserCreationRequest(
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
                whenever(userRepository.save(Mockito.any()))
                    .thenReturn(userCreationRequest.toEntity())

                // when & then
                assertDoesNotThrow {
                    userService.join(userCreationRequest)
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
                val userCreationRequest =
                    UserCreationRequest(
                        loginId = loginId,
                        password = password,
                        name = name,
                        rrn = rrn,
                        phoneNumber = phoneNumber,
                        address = address,
                    )

                whenever(userRepository.findByLoginId(loginId))
                    .thenReturn(userCreationRequest.toEntity())

                // when & then
                assertThrows<ClientBadRequestException> {
                    userService.join(userCreationRequest)
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
                val userCreationRequest =
                    UserCreationRequest(
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
                    .thenReturn(userCreationRequest.toEntity())

                // when & then
                assertThrows<ClientBadRequestException> {
                    userService.join(userCreationRequest)
                }
                    .also { assertEquals(it.message, DUPLICATE_RRN_EXCEPTION_MESSAGE) }
            }
        }
    }
}
