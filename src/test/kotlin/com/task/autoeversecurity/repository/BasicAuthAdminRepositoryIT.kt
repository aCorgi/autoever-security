package com.task.autoeversecurity.repository

import com.task.autoeversecurity.config.IntegrationTestBase
import com.task.autoeversecurity.repository.redis.BasicAuthAdminRepository
import com.task.autoeversecurity.util.Constants.Redis.BASIC_AUTH_USERS_REDIS_KEY
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BasicAuthAdminRepositoryIT : IntegrationTestBase() {
    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    private lateinit var basicAuthAdminRepository: BasicAuthAdminRepository

    @BeforeEach
    @AfterEach
    fun setUp() {
        redisTemplate.keys("*").forEach { redisTemplate.delete(it) }
    }

    @Nested
    inner class `관리자 정보 Redis 저장` {
        @Nested
        inner class `성공` {
            @Test
            fun `관리자 정보를 Redis 에 저장하고 조회한다`() {
                // given
                val name = "admin"
                val password = "password123"

                // when
                basicAuthAdminRepository.setAdminInRedis(name, password)

                // then
                val storedPassword =
                    redisTemplate.opsForHash<String, String>()
                        .get(BASIC_AUTH_USERS_REDIS_KEY, name)
                assertEquals(password, storedPassword)
            }
        }
    }

    @Nested
    inner class `관리자 정보 Redis 조회` {
        @Nested
        inner class `성공` {
            @Test
            fun `존재하는 관리자 이름으로 비밀번호를 조회한다`() {
                // given
                val name = "admin"
                val password = "password123"
                redisTemplate.opsForHash<String, String>()
                    .put(BASIC_AUTH_USERS_REDIS_KEY, name, password)

                // when
                val result = basicAuthAdminRepository.getBasicAuthAdminsByNameOrNull(name)

                // then
                assertEquals(password, result)
            }

            @Test
            fun `존재하지 않는 관리자 이름으로 조회 시 null 을 반환한다`() {
                // given
                val name = "nonexistent"

                // when
                val result = basicAuthAdminRepository.getBasicAuthAdminsByNameOrNull(name)

                // then
                assertNull(result)
            }
        }
    }
}
