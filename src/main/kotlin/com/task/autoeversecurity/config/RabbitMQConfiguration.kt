package com.task.autoeversecurity.config

import com.task.autoeversecurity.property.RabbitMQProperties
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfiguration(
    private val rabbitMQProperties: RabbitMQProperties,
) {
    @Bean
    fun kakaoTalkMessageQueue(): Queue {
        return Queue(rabbitMQProperties.sendMessage.kakaoTalkMessageQueue.name, true)
    }

    @Bean
    fun smsMessageQueue(): Queue {
        return Queue(rabbitMQProperties.sendMessage.smsMessageQueue.name, true)
    }

    @Bean
    fun sendMessageExchange(): DirectExchange {
        return DirectExchange(rabbitMQProperties.sendMessage.exchange)
    }

    @Bean
    fun kakaoTalkMessageBinding(
        kakaoTalkMessageQueue: Queue,
        exchange: DirectExchange,
    ): Binding {
        return BindingBuilder
            .bind(kakaoTalkMessageQueue)
            .to(exchange)
            .with(rabbitMQProperties.sendMessage.kakaoTalkMessageQueue.routingKey)
    }

    @Bean
    fun smsMessageBinding(
        smsMessageQueue: Queue,
        exchange: DirectExchange,
    ): Binding {
        return BindingBuilder
            .bind(smsMessageQueue)
            .to(exchange)
            .with(rabbitMQProperties.sendMessage.smsMessageQueue.routingKey)
    }
}
