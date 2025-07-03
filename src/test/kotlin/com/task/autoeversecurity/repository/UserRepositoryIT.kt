package com.task.autoeversecurity.repository

import com.task.autoeversecurity.config.IntegrationTestBase
import com.task.autoeversecurity.util.MockUserEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserRepositoryIT : IntegrationTestBase() {
    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    @AfterEach
    fun setUp() {
        userRepository.deleteAll()
    }

    @Nested
    inner class `회원 연령대 조회` {
        @Nested
        inner class `성공` {
            @Test
            fun `특정 연령대의 회원을 조회한다`() {
                // given
                val user1 = MockUserEntity.of(name = "User1", age = 29, loginId = "user1", rrn = "123456")
                val user2 = MockUserEntity.of(name = "User2", age = 30, loginId = "user2", rrn = "654321")

                transactional {
                    userRepository.saveAll(listOf(user1, user2))
                }

                // when
                transactional {
                    val result = userRepository.findByAgeBetween(20, 29)

                    // then
                    assertEquals(1, result.size)
                    assertEquals("User1", result[0].name)
                }
            }
        }
    }

    @Nested
    inner class `회원 로그인 ID 조회` {
        @Nested
        inner class `성공` {
            @Test
            fun `로그인 ID로 회원을 조회한다`() {
                // given
                val user = MockUserEntity.of(name = "User1", age = 25, loginId = "user1", rrn = "123456")

                transactional {
                    userRepository.save(user)
                }

                // when
                transactional {
                    val result = userRepository.findByLoginId("user1")

                    // then
                    assertEquals("User1", result?.name)
                }
            }

            @Test
            fun `존재하지 않는 로그인 ID로 조회 시 null 을 반환한다`() {
                // when
                transactional {
                    val result = userRepository.findByLoginId("nonexistent")

                    // then
                    assertNull(result)
                }
            }
        }
    }

    @Nested
    inner class `회원 주민등록번호 조회` {
        @Nested
        inner class `성공` {
            @Test
            fun `주민등록번호로 회원을 조회한다`() {
                // given
                val user = MockUserEntity.of(name = "User1", age = 25, loginId = "user1", rrn = "123456")
                transactional {
                    userRepository.save(user)
                }

                // when
                transactional {
                    val result = userRepository.findByRrn("123456")

                    // then
                    assertEquals("User1", result?.name)
                }
            }

            @Test
            fun `존재하지 않는 주민등록번호로 조회 시 null 을 반환한다`() {
                // when
                transactional {
                    val result = userRepository.findByRrn("nonexistent")

                    // then
                    assertNull(result)
                }
            }
        }
    }
}
