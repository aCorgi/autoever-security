package com.task.autoeversecurity.component

import com.task.autoeversecurity.config.IntegrationTestBase
import com.task.autoeversecurity.util.Constants.Redis.KAKAO_TALK_MESSAGE_RATE_LIMIT
import com.task.autoeversecurity.util.Constants.Redis.SMS_MESSAGE_RATE_LIMIT
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class RateLimiterIT : IntegrationTestBase() {
    @BeforeEach
    fun setUp() {
        redissonClient.keys.deleteByPattern("redisson_rate_limiter:{*}")
    }

    @Nested
    inner class `Rate Limiter 에서 acquire 획득을 시도` {
        @Nested
        inner class `성공` {
            @Test
            fun `KAKAO_TALK_MESSAGE RateLimiter 에서 요청을 성공적으로 획득한다`() {
                // when
                val result = rateLimiter.tryAcquire(RateLimiter.RateLimiterType.KAKAO_TALK_MESSAGE)

                // then
                assertTrue(result)
            }

            @Test
            fun `SMS_MESSAGE RateLimiter 에서 요청을 성공적으로 획득한다`() {
                // when
                val result = rateLimiter.tryAcquire(RateLimiter.RateLimiterType.SMS_MESSAGE)

                // then
                assertTrue(result)
            }
        }

        @Nested
        inner class `실패` {
            @Test
            fun `KAKAO_TALK_MESSAGE RateLimiter 에서 요청 획득 실패 시 false 를 반환한다`() {
                // given
                repeat(KAKAO_TALK_MESSAGE_RATE_LIMIT.toInt()) {
                    rateLimiter.tryAcquire(RateLimiter.RateLimiterType.KAKAO_TALK_MESSAGE)
                }

                // when
                val result = rateLimiter.tryAcquire(RateLimiter.RateLimiterType.KAKAO_TALK_MESSAGE)

                // then
                assertFalse(result)
            }

            @Test
            fun `SMS_MESSAGE RateLimiter 에서 요청 획득 실패 시 false 를 반환한다`() {
                // given
                repeat(SMS_MESSAGE_RATE_LIMIT.toInt()) {
                    rateLimiter.tryAcquire(RateLimiter.RateLimiterType.SMS_MESSAGE)
                }

                // when
                val result = rateLimiter.tryAcquire(RateLimiter.RateLimiterType.SMS_MESSAGE)

                // then
                assertFalse(result)
            }
        }
    }
}
