package com.task.autoeversecurity.component

import com.task.autoeversecurity.exception.ClientBadRequestException
import com.task.autoeversecurity.util.Constants.Redis.KAKAO_TALK_MESSAGE_RATE_LIMIT
import com.task.autoeversecurity.util.Constants.Redis.KAKAO_TALK_MESSAGE_RATE_LIMITER_NAME
import com.task.autoeversecurity.util.Constants.Redis.SMS_MESSAGE_RATE_LIMIT
import com.task.autoeversecurity.util.Constants.Redis.SMS_MESSAGE_RATE_LIMITER_NAME
import org.redisson.api.RateType
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RateLimiter(
    private val redissonClient: RedissonClient,
) {
    private val kakaoTalkMessageRateLimiter = redissonClient.getRateLimiter(KAKAO_TALK_MESSAGE_RATE_LIMITER_NAME)
    private val smsMessageRateLimiter = redissonClient.getRateLimiter(SMS_MESSAGE_RATE_LIMITER_NAME)

    fun startMessageRateLimiters() {
        startKakaoTalkMessageRateLimiter()
        startSmsMessageRateLimiter()
    }

    // FIXME: 추상화
    fun tryAcquire(limiterName: String): Boolean {
        return when (limiterName) {
            KAKAO_TALK_MESSAGE_RATE_LIMITER_NAME -> {
                kakaoTalkMessageRateLimiter.tryAcquire(3, Duration.ofSeconds(3))
            }
            SMS_MESSAGE_RATE_LIMITER_NAME -> {
                smsMessageRateLimiter.tryAcquire(3, Duration.ofSeconds(3))
            }
            else -> throw ClientBadRequestException("Unknown rate limiter: $limiterName")
        }
    }

    private fun startKakaoTalkMessageRateLimiter() {
        kakaoTalkMessageRateLimiter.trySetRate(
            RateType.OVERALL,
            KAKAO_TALK_MESSAGE_RATE_LIMIT,
            Duration.ofMinutes(1),
        )
    }

    private fun startSmsMessageRateLimiter() {
        smsMessageRateLimiter.trySetRate(
            RateType.OVERALL,
            SMS_MESSAGE_RATE_LIMIT,
            Duration.ofMinutes(1),
        )
    }
}
