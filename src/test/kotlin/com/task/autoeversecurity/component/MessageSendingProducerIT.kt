package com.task.autoeversecurity.component

import com.fasterxml.jackson.module.kotlin.readValue
import com.task.autoeversecurity.config.IntegrationTestBase
import com.task.autoeversecurity.dto.message.SendKakaoTalkMessageDto
import com.task.autoeversecurity.dto.message.SendSmsMessageDto
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.springframework.test.context.TestPropertySource
import java.time.Duration
import kotlin.test.Test

@Disabled
@TestPropertySource(
    properties = [
        "spring.rabbitmq.listener.simple.auto-startup=false",
    ],
)
class MessageSendingProducerIT : IntegrationTestBase() {
    @BeforeEach
    fun setLateLimit() {
        rateLimiter.startMessageRateLimiters()
        amqpAdmin.purgeQueue(rabbitMQProperties.sendMessage.kakaoTalkMessageQueue.name, false)
        amqpAdmin.purgeQueue(rabbitMQProperties.sendMessage.smsMessageQueue.name, false)
    }

    @AfterEach
    fun deleteLateLimit() {
        redissonClient.keys.deleteByPattern("redisson_rate_limiter:{*}")
    }

    @Nested
    inner class `카카오톡 메세지 전송 요청을 MQ 에 produce` {
        @Nested
        inner class `성공` {
            @Test
            fun `MQ 에 잘 전송되었다`() {
                // given
                val kakaoTalkMessageSendingDto = SendKakaoTalkMessageDto("01012341923", "카톡왔숑")

                kakaoTalkApiMockWebServer.enqueue(getSuccessMockResponse())

                // when
                messageSendingProducer.sendKakaoTalkMessage(kakaoTalkMessageSendingDto)

                // then
                Awaitility.await()
                    .atMost(Duration.ofSeconds(5))
                    .pollInterval(Duration.ofSeconds(1))
                    .untilAsserted {
                        val message = rabbitTemplate.receive(rabbitMQProperties.sendMessage.kakaoTalkMessageQueue.name)
                        assertThat(message).isNotNull
                        val messageBody = String(message!!.body)

                        val parsedMessage = objectMapper.readValue<SendKakaoTalkMessageDto>(messageBody)

                        assertThat(parsedMessage.phone).isEqualTo(kakaoTalkMessageSendingDto.phone)
                        assertThat(parsedMessage.content).isEqualTo(kakaoTalkMessageSendingDto.content)
                    }
            }
        }
    }

    @Nested
    inner class `SMS 메세지 전송 요청을 MQ produce` {
        @Nested
        inner class `성공` {
            @Test
            fun `MQ 에 잘 전송되었다`() {
                // given
                val smsMessageSendingDto = SendSmsMessageDto("01012341923", "문자왔숑")

                smsApiMockWebServer.enqueue(getSuccessMockResponse())

                // when
                messageSendingProducer.sendSmsMessage(smsMessageSendingDto)

                // then
                Awaitility.await()
                    .atMost(Duration.ofSeconds(5))
                    .pollInterval(Duration.ofSeconds(1))
                    .untilAsserted {
                        val message = rabbitTemplate.receive(rabbitMQProperties.sendMessage.smsMessageQueue.name)
                        assertThat(message).isNotNull
                        val messageBody = String(message!!.body)

                        val parsedMessage = objectMapper.readValue<SendKakaoTalkMessageDto>(messageBody)

                        assertThat(parsedMessage.phone).isEqualTo(smsMessageSendingDto.phone)
                        assertThat(parsedMessage.content).isEqualTo(smsMessageSendingDto.content)
                    }
            }
        }
    }
}
