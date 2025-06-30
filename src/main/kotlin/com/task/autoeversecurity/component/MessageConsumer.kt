package com.task.autoeversecurity.component

import com.task.autoeversecurity.property.RabbitMQProperties
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class MessageConsumer(
    private val rabbitMQProperties: RabbitMQProperties,
) {
    @RabbitListener(queues = ["\${rabbitmq.sendMessage.kakaoTalkMessageQueue.name}"])
    fun receiveKakaoTalkMessage(message: String) {
        println("Received message: $message")
        // 여기서 rate limiter 체크해서 처리 제한도 가능
    }

    @RabbitListener(queues = ["\${rabbitmq.sendMessage.smsMessageQueue.name}"])
    fun receiveSmsMessage(message: String) {
        println("Received message: $message")
        // 여기서 rate limiter 체크해서 처리 제한도 가능
    }
}
