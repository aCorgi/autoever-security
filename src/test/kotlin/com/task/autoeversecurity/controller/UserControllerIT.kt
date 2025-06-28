package com.task.autoeversecurity.controller

import com.task.autoeversecurity.config.ControllerTestBase
import com.task.autoeversecurity.dto.UserJoinRequest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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
}
