package com.task.autoeversecurity.component

import com.task.autoeversecurity.property.RabbitMQProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class MessageProducer(
    private val rabbitMQProperties: RabbitMQProperties,
    private val rabbitTemplate: RabbitTemplate,
) {
    fun sendKakaoTalkMessage(message: String) {
        rabbitTemplate.convertAndSend(
            rabbitMQProperties.sendMessage.exchange,
            rabbitMQProperties.sendMessage.kakaoTalkMessageQueue.routingKey,
            message,
        )
    }

    fun sendSmsMessage(message: String) {
        rabbitTemplate.convertAndSend(
            rabbitMQProperties.sendMessage.exchange,
            rabbitMQProperties.sendMessage.smsMessageQueue.routingKey,
            message,
        )
    }
}
