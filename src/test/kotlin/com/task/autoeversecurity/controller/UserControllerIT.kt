package com.task.autoeversecurity.controller

import com.task.autoeversecurity.config.ControllerTestBase
import com.task.autoeversecurity.dto.UserJoinRequest
import com.task.autoeversecurity.dto.UserLoginRequest
import com.task.autoeversecurity.util.CommonUtils.getBasicAuthToken
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class UserControllerIT : ControllerTestBase() {
    @Nested
    inner class `회원 가입` {
        private val url = "/users/join"

        @Nested
        inner class `성공` {
            @Test
            fun `회원가입 성공 시 201 CREATED 를 반환한다`() {
                // given
                val request =
                    UserJoinRequest(
                        loginId = "testUser",
                        password = "securePassword123",
                        name = "John Doe",
                        rrn = "9101011234212",
                        phoneNumber = "01012345678",
                        address = "Seoul, Korea",
                    )

                // when & then
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                }
            }
        }
    }

    @Nested
    inner class `로그인` {
        private val url = "/users/login"

        @Nested
        inner class `성공` {
            @Test
            fun `로그인 성공 시 Basic Auth 토큰을 반환한다`() {
                // given
                val request =
                    UserLoginRequest(
                        loginId = "testUser",
                        password = "securePassword123",
                    )

                whenever(userService.login(request))
                    .thenReturn(getBasicAuthToken(request.loginId, request.password))

                // when & then
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    content {
                        val expectedToken = getBasicAuthToken(request.loginId, request.password)
                        string(expectedToken)
                    }
                }
            }
        }

        @Nested
        inner class `실패` {
            @Test
            fun `존재하지 않는 loginId 로 로그인 시 404 NOT FOUND 를 반환한다`() {
                // given
                val request =
                    UserLoginRequest(
                        loginId = "nonExistentUser",
                        password = "securePassword123",
                    )

                // when & then
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isNotFound() }
                }
            }

            @Test
            fun `잘못된 비밀번호로 로그인 시 400 BAD REQUEST 를 반환한다`() {
                // given
                val request =
                    UserLoginRequest(
                        loginId = "testUser",
                        password = "wrongPassword",
                    )

                // when & then
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }
}
