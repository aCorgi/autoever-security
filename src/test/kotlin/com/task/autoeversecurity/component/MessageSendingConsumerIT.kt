package com.task.autoeversecurity.component

import com.task.autoeversecurity.config.IntegrationTestBase
import com.task.autoeversecurity.dto.message.SendKakaoTalkMessageDto
import com.task.autoeversecurity.dto.message.SendSmsMessageDto
import com.task.autoeversecurity.util.Constants
import org.awaitility.Awaitility
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.test.context.TestPropertySource
import java.time.Duration
import kotlin.test.Test

@TestPropertySource(properties = ["spring.rabbitmq.listener.simple.auto-startup=true"])
class MessageSendingConsumerIT : IntegrationTestBase() {
    @BeforeEach
    fun setLateLimit() {
        rateLimiter.startMessageRateLimiters()
    }

    @AfterEach
    fun deleteLateLimit() {
        redissonClient.keys.deleteByPattern("redisson_rate_limiter:{*}")
    }

    @Nested
    inner class `카카오톡 메세지 큐 consume` {
        @Nested
        inner class `성공` {
            @Test
            fun `카카오톡 메세지를 MQ 에 전송하면 Consumer 가 받아서 KakaoTalk API 를 호출한다`() {
                // given
                val dto = SendKakaoTalkMessageDto("01012345678", "테스트 카카오톡 메시지")
                val messageBody = objectMapper.writeValueAsString(dto)

                // when
                kakaoTalkApiMockWebServer.enqueue(getSuccessMockResponse())

                rabbitTemplate.convertAndSend(
                    rabbitMQProperties.sendMessage.exchange,
                    rabbitMQProperties.sendMessage.kakaoTalkMessageQueue.routingKey,
                    messageBody,
                )

                // then
                Awaitility.await()
                    .atMost(Duration.ofSeconds(5))
                    .pollInterval(Duration.ofSeconds(1))
                    .untilAsserted {
                        verify(rateLimiter, times(1))
                            .tryAcquire(RateLimiter.RateLimiterType.KAKAO_TALK_MESSAGE)

                        verify(messageSendingProducer, never())
                            .sendSmsMessage(
                                SendSmsMessageDto(
                                    phone = dto.phone,
                                    content = dto.content,
                                ),
                            )
                    }
            }

            @DisplayName("카카오 메세지 1분에 ${Constants.Redis.KAKAO_TALK_MESSAGE_RATE_LIMIT} 회 이상 호출하면 nack 상태로 requeue 한다")
            @Test
            fun `제한된 메세지 전송 횟수를 초과하면, nack 상태로 requeue 한다`() {
            }
        }
    }

    @Nested
    inner class `SMS 메세지 큐 consume` {
        @Nested
        inner class `성공` {
            @Test
            fun `SMS 메세지를 MQ 에 전송하면 Consumer 가 받아서 SMS API 를 호출한다`() {
                // given
                val dto = SendSmsMessageDto("01012345678", "테스트 SMS 메시지")
                val messageBody = objectMapper.writeValueAsString(dto)

                // when
                smsApiMockWebServer.enqueue(getSuccessMockResponse())

                rabbitTemplate.convertAndSend(
                    rabbitMQProperties.sendMessage.exchange,
                    rabbitMQProperties.sendMessage.smsMessageQueue.routingKey,
                    messageBody,
                )

                // then
                Awaitility.await()
                    .atMost(Duration.ofSeconds(5))
                    .pollInterval(Duration.ofSeconds(1))
                    .untilAsserted {
                        verify(rateLimiter, times(1))
                            .tryAcquire(RateLimiter.RateLimiterType.SMS_MESSAGE)

                        verify(messageSendingProducer, never())
                            .sendSmsMessage(
                                SendSmsMessageDto(
                                    phone = dto.phone,
                                    content = dto.content,
                                ),
                            )
                    }
            }
        }
    }
}
