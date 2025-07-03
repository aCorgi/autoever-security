package com.task.autoeversecurity.service

import com.task.autoeversecurity.component.MessageSendingProducer
import com.task.autoeversecurity.config.UnitTestBase
import com.task.autoeversecurity.dto.AgeGroup
import com.task.autoeversecurity.dto.UserUpdateRequest
import com.task.autoeversecurity.util.MockUserEntity
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.Test
import kotlin.test.assertEquals

class AdminServiceTest : UnitTestBase() {
    @InjectMocks
    private lateinit var adminService: AdminService

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var messageSendingProducer: MessageSendingProducer

    @Nested
    inner class `연령대별 카카오톡 메시지 전송` {
        @Nested
        inner class `성공` {
            @Test
            fun `유효한 연령대 값으로 메시지를 전송한다`() {
                // given
                val ageGroup = AgeGroup.AGE_20S
                val users =
                    listOf(
                        MockUserEntity.of(phoneNumber = "01012345678", name = "User1"),
                        MockUserEntity.of(phoneNumber = "01098765432", name = "User2"),
                    )

                whenever(userService.findByAgeGroup(ageGroup))
                    .thenReturn(users)

                // when
                adminService.sendKakaoMessageByAgeGroup(ageGroup)

                // then
                verify(messageSendingProducer, times(2))
                    .sendKakaoTalkMessage(any())
            }
        }
    }

    @Nested
    inner class `회원 목록 페이징 조회` {
        @Nested
        inner class `성공` {
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

                whenever(userService.getPagedUsers(pageable))
                    .thenReturn(pageResponse)

                // when
                val result = adminService.getPagedUsers(pageable)

                // then
                assertEquals(users.size, result.content.size)
            }
        }
    }

    @Nested
    inner class `회원 정보 수정` {
        @Nested
        inner class `성공` {
            @Test
            fun `유효한 요청으로 회원 정보를 수정한다`() {
                // given
                val userId = 1
                val request =
                    UserUpdateRequest(
                        userId = userId,
                        password = "newPassword",
                        address = null,
                    )
                val user = MockUserEntity.of(id = userId)

                whenever(userService.findById(userId))
                    .thenReturn(user)
                whenever(passwordEncoder.encode(request.password!!))
                    .thenReturn("encodedPassword")

                // when
                adminService.updateUser(request)

                // then
                assertEquals("encodedPassword", user.password)
            }
        }
    }

    @Nested
    inner class `회원 삭제` {
        @Nested
        inner class `성공` {
            @Test
            fun `유효한 회원 ID로 회원을 삭제한다`() {
                // given
                val userId = 1

                doNothing().whenever(userService)
                    .deleteById(userId)

                // when
                assertDoesNotThrow {
                    adminService.deleteUser(userId)
                }
            }
        }
    }
}
