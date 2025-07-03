package com.task.autoeversecurity.controller

import com.task.autoeversecurity.config.AutoeverAuthority
import com.task.autoeversecurity.config.AutoeverMember
import com.task.autoeversecurity.config.ControllerTestBase
import com.task.autoeversecurity.config.WithMockAutoeverMember
import com.task.autoeversecurity.dto.AddressDto
import com.task.autoeversecurity.dto.MyselfUserResponse
import com.task.autoeversecurity.dto.UserJoinRequest
import com.task.autoeversecurity.dto.UserLoginRequest
import com.task.autoeversecurity.util.CommonUtils.getBasicAuthToken
import com.task.autoeversecurity.util.MockUserEntity
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class UserControllerIT : ControllerTestBase() {
    @Nested
    inner class `내 정보 조회` {
        private val url = "/users/myself"

        @Nested
        inner class `성공` {
            @WithMockAutoeverMember(autoeverAuthorities = [AutoeverAuthority.USER])
            @Test
            fun `유효한 인증 정보로 내 정보를 조회하면 200 OK 를 반환한다`() {
                // given
                val user = MockUserEntity.of()
                val response = MyselfUserResponse(user)
                val autoeverMember =
                    SecurityContextHolder.getContext().authentication
                        .principal as AutoeverMember

                whenever(userService.getMyself(autoeverMember))
                    .thenReturn(response)

                // when & then
                mockMvc.get(url)
                    .andExpect {
                        status { isOk() }
                        content {
                            json(objectMapper.writeValueAsString(response))
                        }
                    }
            }
        }

        @Nested
        inner class `실패` {
            @Test
            fun `인증 정보가 없으면 401 UNAUTHORIZED 를 반환한다`() {
                // when & then
                mockMvc.get(url)
                    .andExpect {
                        status { isUnauthorized() }
                    }
            }

            @WithMockAutoeverMember(autoeverAuthorities = [AutoeverAuthority.ADMIN])
            @Test
            fun `사용자 권한이 아니면 403 오류를 반환한다`() {
                // when & then
                mockMvc.get(url)
                    .andExpect {
                        status { isForbidden() }
                    }
            }
        }
    }

    @Nested
    inner class `회원 가입` {
        private val url = "/users/join"

        @Nested
        inner class `성공` {
            @Test
            fun `회원가입 성공 시 201 CREATED 를 반환한다`() {
                // given
                val addressDto =
                    AddressDto(
                        city = "서울시",
                        district = "관악구",
                        town = "청룡 7길 50",
                    )
                val request =
                    UserJoinRequest(
                        loginId = "testUser",
                        password = "securePassword123",
                        name = "John Doe",
                        rrn = "9101011234212",
                        phoneNumber = "01012345678",
                        address = addressDto,
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
            fun `blank password 로 로그인 시 400 BAD REQUEST 를 반환한다`() {
                // given
                val request =
                    UserLoginRequest(
                        loginId = "nonExistentUser",
                        password = " ",
                    )

                // when & then
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            fun `blank loginId 로 로그인 시 400 BAD REQUEST 를 반환한다`() {
                // given
                val request =
                    UserLoginRequest(
                        loginId = " ",
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
