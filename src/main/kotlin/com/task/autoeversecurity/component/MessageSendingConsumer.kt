package com.task.autoeversecurity.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.task.autoeversecurity.client.KakaoTalkApiClient
import com.task.autoeversecurity.client.SmsApiClient
import com.task.autoeversecurity.dto.api.SendKakaoTalkApiRequest
import com.task.autoeversecurity.dto.api.SendSmsApiRequest
import com.task.autoeversecurity.dto.message.SendKakaoTalkMessageDto
import com.task.autoeversecurity.dto.message.SendSmsMessageDto
import com.task.autoeversecurity.util.logger
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class MessageSendingConsumer(
    private val objectMapper: ObjectMapper,
    private val kakaoTalkApiClient: KakaoTalkApiClient,
    private val smsApiClient: SmsApiClient,
    private val messageSendingProducer: MessageSendingProducer,
) {
    val log = logger<MessageSendingConsumer>()

    @RabbitListener(queues = ["\${rabbit-mq.send-message.kakao-talk-message-queue.name}"])
    fun receiveKakaoTalkMessage(message: String) {
        // 여기서 rate limiter 체크해서 처리 제한도 가능

        val sendKakaoTalkMessageDto = objectMapper.readValue<SendKakaoTalkMessageDto>(message)

        runCatching {
            kakaoTalkApiClient.sendKakaoTalkMessage(SendKakaoTalkApiRequest(sendKakaoTalkMessageDto))
        }
            .onFailure {
                log.warn("카카오톡 메시지 전송 실패. SMS 로 대신 발송합니다. (exception Message : ${it.message})", it)

                messageSendingProducer.sendSmsMessage(
                    SendSmsMessageDto(
                        phone = sendKakaoTalkMessageDto.phone,
                        message = sendKakaoTalkMessageDto.message,
                    ),
                )
            }
    }

    @RabbitListener(queues = ["\${rabbit-mq.send-message.sms-message-queue.name}"])
    fun receiveSmsMessage(message: String) {
        val sendSmsMessageDto = objectMapper.readValue<SendSmsMessageDto>(message)

        // 여기서 rate limiter 체크해서 처리 제한도 가능

        runCatching {
            smsApiClient.sendSms(
                phone = sendSmsMessageDto.phone,
                request = SendSmsApiRequest(sendSmsMessageDto.message),
            )
        }
    }
}
