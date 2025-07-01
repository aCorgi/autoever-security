package com.task.autoeversecurity.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rabbitmq.client.Channel
import com.task.autoeversecurity.client.KakaoTalkApiClient
import com.task.autoeversecurity.client.SmsApiClient
import com.task.autoeversecurity.dto.api.SendKakaoTalkApiRequest
import com.task.autoeversecurity.dto.api.SendSmsApiRequest
import com.task.autoeversecurity.dto.message.SendKakaoTalkMessageDto
import com.task.autoeversecurity.dto.message.SendSmsMessageDto
import com.task.autoeversecurity.util.logger
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class MessageSendingConsumer(
    private val objectMapper: ObjectMapper,
    private val kakaoTalkApiClient: KakaoTalkApiClient,
    private val smsApiClient: SmsApiClient,
    private val rateLimiter: RateLimiter,
    private val messageSendingProducer: MessageSendingProducer,
) {
    val log = logger<MessageSendingConsumer>()

    @RabbitListener(queues = ["\${rabbit-mq.send-message.kakao-talk-message-queue.name}"], ackMode = "MANUAL")
    fun receiveKakaoTalkMessage(
        message: Message,
        channel: Channel,
    ) {
        val messageBody = String(message.body)

        if (rateLimiter.tryAcquire(RateLimiter.RateLimiterType.KAKAO_TALK_MESSAGE).not()) {
            log.warn("제한된 카카오톡 메세지 전송 횟수를 초과했습니다. Requeue 합니다.")

            channel.basicNack(message.messageProperties.deliveryTag, false, true)
            return
        }

        val sendKakaoTalkMessageDto = objectMapper.readValue<SendKakaoTalkMessageDto>(messageBody)

        runCatching {
            kakaoTalkApiClient.sendKakaoTalkMessage(SendKakaoTalkApiRequest(sendKakaoTalkMessageDto))
        }
            .onFailure {
                log.warn("카카오톡 메시지 전송 실패. SMS 로 대신 발송합니다. (exception Message : ${it.message})", it)

                messageSendingProducer.sendSmsMessage(
                    SendSmsMessageDto(
                        phone = sendKakaoTalkMessageDto.phone,
                        content = sendKakaoTalkMessageDto.content,
                    ),
                )
            }
    }

    @RabbitListener(queues = ["\${rabbit-mq.send-message.sms-message-queue.name}"])
    fun receiveSmsMessage(
        message: Message,
        channel: Channel,
    ) {
        val messageBody = String(message.body)

        if (rateLimiter.tryAcquire(RateLimiter.RateLimiterType.SMS_MESSAGE).not()) {
            log.warn("제한된 SMS 메세지 전송 횟수를 초과했습니다. Requeue 합니다.")

            channel.basicNack(message.messageProperties.deliveryTag, false, true)
            return
        }

        val sendSmsMessageDto = objectMapper.readValue<SendSmsMessageDto>(messageBody)

        runCatching {
            smsApiClient.sendSms(
                phone = sendSmsMessageDto.phone,
                request = SendSmsApiRequest(sendSmsMessageDto.content),
            )
        }
    }
}
