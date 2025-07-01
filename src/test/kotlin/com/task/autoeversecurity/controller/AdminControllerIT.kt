package com.task.autoeversecurity.controller

import com.task.autoeversecurity.config.AutoeverAuthority
import com.task.autoeversecurity.config.ControllerTestBase
import com.task.autoeversecurity.config.WithMockAutoeverMember
import com.task.autoeversecurity.dto.UserUpdateRequest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.patch
import kotlin.test.Test

class AdminControllerIT : ControllerTestBase() {
    @Nested
    inner class `회원 정보 수정` {
        private val url = "/admins"

        @WithMockAutoeverMember(autoeverAuthorities = [AutoeverAuthority.ADMIN])
        @Nested
        inner class `성공` {
            @Test
            fun `회원 정보 수정에 성공하면 204 NO CONTENT 를 반환한다`() {
                // given
                val request =
                    UserUpdateRequest(
                        userId = 20,
                        password = "newPassword123",
                        address = null,
                    )

                // when & then
                mockMvc.patch(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isNoContent() }
                }

                verify(adminService, times(1))
                    .updateUser(request)
            }
        }

        @Nested
        inner class `실패` {
            @WithMockAutoeverMember(autoeverAuthorities = [AutoeverAuthority.USER])
            @Test
            fun `어드민 권한이 아니면 403 오류를 반환한다`() {
                // given
                val request =
                    UserUpdateRequest(
                        userId = 123,
                        password = "newPassword123",
                        address = null,
                    )

                // when & then
                mockMvc.patch(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isForbidden() }
                }
            }

            @Test
            fun `권한이 없으면 401 오류를 반환한다`() {
                // given
                val request =
                    UserUpdateRequest(
                        userId = 123,
                        password = "newPassword123",
                        address = null,
                    )

                // when & then
                mockMvc.patch(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isUnauthorized() }
                }
            }

            @WithMockAutoeverMember(autoeverAuthorities = [AutoeverAuthority.ADMIN])
            @ParameterizedTest
            @ValueSource(ints = [0, -1])
            fun `userId 가 1보다 작으면 400 BAD REQUEST 를 반환한다`(userId: Int) {
                // given
                val request =
                    UserUpdateRequest(
                        userId = userId,
                        password = "newPassword123",
                        address = null,
                    )

                // when & then
                mockMvc.patch(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }

    @Nested
    inner class `회원 삭제` {
        private val url = "/admins"

        @WithMockAutoeverMember(autoeverAuthorities = [AutoeverAuthority.ADMIN])
        @Nested
        inner class `성공` {
            private val userId = 123

            @Test
            fun `회원 삭제에 성공하면 204 NO CONTENT 를 반환한다`() {
                // given
                val request = mapOf("userId" to userId)

                // when & then
                mockMvc.delete(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isNoContent() }
                }

                verify(adminService, times(1))
                    .deleteUser(userId)
            }
        }

        @Nested
        inner class `실패` {
            private val userId = 1232

            @WithMockAutoeverMember(autoeverAuthorities = [AutoeverAuthority.USER])
            @Test
            fun `어드민 권한이 아니면 403 오류를 반환한다`() {
                // given
                val request = mapOf("userId" to userId)

                // when & then
                mockMvc.delete(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isForbidden() }
                }
            }

            @Test
            fun `권한이 없으면 401 오류를 반환한다`() {
                // given
                val request = mapOf("userId" to userId)

                // when & then
                mockMvc.delete(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isUnauthorized() }
                }
            }

            @WithMockAutoeverMember(autoeverAuthorities = [AutoeverAuthority.ADMIN])
            @ParameterizedTest
            @ValueSource(ints = [0, -1])
            fun `userId 가 1보다 작으면 400 BAD REQUEST 를 반환한다`(userId: Int) {
                // given
                val request = mapOf("userId" to userId)

                // when & then
                mockMvc.delete(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }
}
