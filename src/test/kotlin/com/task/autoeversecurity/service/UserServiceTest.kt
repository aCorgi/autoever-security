package com.task.autoeversecurity.service

import com.task.autoeversecurity.component.Aes256EncryptionManager
import com.task.autoeversecurity.config.AutoeverAuthority
import com.task.autoeversecurity.config.AutoeverMember
import com.task.autoeversecurity.config.UnitTestBase
import com.task.autoeversecurity.dto.AddressDto
import com.task.autoeversecurity.dto.AgeGroup
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
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertEquals

class UserServiceTest : UnitTestBase() {
    @InjectMocks
    private lateinit var userService: UserService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var aes256EncryptionManager: Aes256EncryptionManager

    @Nested
    inner class `회원가입` {
        @Nested
        inner class `성공` {
            @Test
            fun `중복이 없는 유효한 요청 값임을 검증하고 User 데이터를 create 한다`() {
                // given
                val loginId = "validUser"
                val password = "securePassword"
                val name = "이해원"
                val rrn = "9101011234924"
                val phoneNumber = "01012345678"
                val addressDto =
                    AddressDto(
                        city = "서울시",
                        district = "관악구",
                        town = "청룡 7길 50",
                    )
                val userJoinRequest =
                    UserJoinRequest(
                        loginId = loginId,
                        password = password,
                        name = name,
                        rrn = rrn,
                        phoneNumber = phoneNumber,
                        address = addressDto,
                    )
                val user =
                    MockUserEntity.of(
                        loginId = loginId,
                        password = "encryptedPassword",
                        name = name,
                        rrn = rrn,
                        phoneNumber = phoneNumber,
                        address = addressDto.toEmbeddable(),
                    )

                whenever(userRepository.findByLoginId(loginId))
                    .thenReturn(null)
                whenever(userRepository.findByRrn(rrn))
                    .thenReturn(null)
                whenever(passwordEncoder.encode(userJoinRequest.password))
                    .thenReturn("encryptedPassword")
                whenever(userRepository.save(Mockito.any()))
                    .thenReturn(user)

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
                val addressDto =
                    AddressDto(
                        city = "서울시",
                        district = "관악구",
                        town = "청룡 7길 50",
                    )
                val userJoinRequest =
                    UserJoinRequest(
                        loginId = loginId,
                        password = password,
                        name = name,
                        rrn = rrn,
                        phoneNumber = phoneNumber,
                        address = addressDto,
                    )
                val user =
                    MockUserEntity.of(
                        loginId = loginId,
                        password = "encryptedPassword",
                        name = name,
                        rrn = rrn,
                        phoneNumber = phoneNumber,
                        address = addressDto.toEmbeddable(),
                    )

                whenever(userRepository.findByLoginId(loginId))
                    .thenReturn(user)

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
                val name = "이해원"
                val rrn = "9101011234924"
                val phoneNumber = "01012345678"
                val addressDto =
                    AddressDto(
                        city = "서울시",
                        district = "관악구",
                        town = "청룡 7길 50",
                    )
                val userJoinRequest =
                    UserJoinRequest(
                        loginId = loginId,
                        password = password,
                        name = name,
                        rrn = rrn,
                        phoneNumber = phoneNumber,
                        address = addressDto,
                    )
                val user =
                    MockUserEntity.of(
                        loginId = loginId,
                        password = "encryptedPassword",
                        name = name,
                        rrn = rrn,
                        phoneNumber = phoneNumber,
                        address = addressDto.toEmbeddable(),
                    )

                whenever(userRepository.findByLoginId(loginId))
                    .thenReturn(null)
                whenever(userRepository.findByRrn(rrn))
                    .thenReturn(user)

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
            @OptIn(ExperimentalEncodingApi::class)
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
                assertEquals("Basic ${Base64.encode("$loginId:$password".encodeToByteArray())}", token)
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
                    MockUserEntity.of(
                        loginId = loginId,
                        password = encryptedPassword,
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

    @Nested
    inner class `회원 삭제` {
        @Nested
        inner class `성공` {
            @Test
            fun `존재하는 회원 ID로 삭제에 성공한다`() {
                // given
                val userId = 1
                val user = MockUserEntity.of(id = userId)

                whenever(userRepository.findById(userId))
                    .thenReturn(Optional.of(user))

                // when & then
                assertDoesNotThrow {
                    userService.deleteById(userId)
                }

                verify(userRepository, times(1))
                    .deleteById(userId)
            }
        }

        @Nested
        inner class `실패` {
            @Test
            fun `존재하지 않는 회원 ID로 삭제 시 예외를 던진다`() {
                // given
                val userId = 999

                whenever(userRepository.findById(userId))
                    .thenReturn(Optional.empty())

                // when & then
                assertThrows<ResourceNotFoundException> {
                    userService.deleteById(userId)
                }
                    .also { assertEquals(it.message, USER_NOT_FOUND_EXCEPTION_MESSAGE) }
            }
        }
    }

    @Nested
    inner class `연령대별 회원 조회` {
        @Test
        fun `유효한 연령대 값으로 회원 목록을 조회한다`() {
            // given
            val ageGroup = AgeGroup.AGE_10S
            val users =
                listOf(
                    MockUserEntity.of(age = 25),
                    MockUserEntity.of(age = 28),
                )

            whenever(userRepository.findByAgeBetween(ageGroup.minAge, ageGroup.maxAge))
                .thenReturn(users)

            // when
            val result = userService.findByAgeGroup(ageGroup)

            // then
            assertEquals(users, result)
            verify(userRepository, times(1)).findByAgeBetween(ageGroup.minAge, ageGroup.maxAge)
        }
    }

    @Nested
    inner class `회원 목록 페이징 조회` {
        @Test
        fun `유효한 Pageable 로 회원 목록을 조회한다`() {
            // given
            val pageable = Pageable.ofSize(10).withPage(0)
            val users =
                listOf(
                    MockUserEntity.of(id = 1),
                    MockUserEntity.of(id = 2),
                )
            val pageResponse = PageImpl(users, pageable, users.size.toLong())

            whenever(userRepository.findAll(pageable))
                .thenReturn(pageResponse)

            // when
            val result = userService.getPagedUsers(pageable)

            // then
            assertEquals(pageResponse, result)
        }
    }

    @Nested
    inner class `회원 정보 조회` {
        @Test
        fun `유효한 AutoeverMember 로 회원 정보를 조회한다`() {
            // given
            val userId = 1
            val user = MockUserEntity.of(id = userId)
            val autoeverMember =
                AutoeverMember(
                    userId = userId,
                    loginId = user.loginId,
                    roles = listOf(AutoeverAuthority.USER),
                )

            whenever(userRepository.findById(userId))
                .thenReturn(Optional.of(user))

            // when
            val result = userService.getMyself(autoeverMember)

            // then
            assertEquals(user.id, result.id)
            assertEquals(user.name, result.name)
        }

        @Test
        fun `존재하지 않는 회원 ID로 조회 시 예외를 던진다`() {
            // given
            val userId = 999
            val autoeverMember =
                AutoeverMember(
                    userId = userId,
                    loginId = "anonymous@naver.com",
                    roles = listOf(AutoeverAuthority.USER),
                )

            whenever(userRepository.findById(userId))
                .thenReturn(Optional.empty())

            // when & then
            assertThrows<ResourceNotFoundException> {
                userService.getMyself(autoeverMember)
            }
                .also { assertEquals(it.message, USER_NOT_FOUND_EXCEPTION_MESSAGE) }
        }
    }
}
