package com.task.autoeversecurity.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("rabbit-mq")
data class RabbitMQProperties(
    val sendMessage: SendMessageQueueProperties,
) {
    data class SendMessageQueueProperties(
        val exchange: String,
        val kakaoTalkMessageQueue: MessageQueueProperties,
        val smsMessageQueue: MessageQueueProperties,
    )

    data class MessageQueueProperties(
        val name: String,
        val routingKey: String,
    )
}
