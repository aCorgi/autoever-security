package com.task.autoeversecurity.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.task.autoeversecurity.dto.message.SendKakaoTalkMessageDto
import com.task.autoeversecurity.dto.message.SendSmsMessageDto
import com.task.autoeversecurity.property.RabbitMQProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class MessageSendingProducer(
    private val objectMapper: ObjectMapper,
    private val rabbitMQProperties: RabbitMQProperties,
    private val rabbitTemplate: RabbitTemplate,
) {
    fun sendKakaoTalkMessage(messageDto: SendKakaoTalkMessageDto) {
        val jsonMessage = objectMapper.writeValueAsString(messageDto)

        rabbitTemplate.convertAndSend(
            rabbitMQProperties.sendMessage.exchange,
            rabbitMQProperties.sendMessage.kakaoTalkMessageQueue.routingKey,
            jsonMessage,
        )
    }

    fun sendSmsMessage(messageDto: SendSmsMessageDto) {
        val jsonMessage = objectMapper.writeValueAsString(messageDto)

        rabbitTemplate.convertAndSend(
            rabbitMQProperties.sendMessage.exchange,
            rabbitMQProperties.sendMessage.smsMessageQueue.routingKey,
            jsonMessage,
        )
    }
}
