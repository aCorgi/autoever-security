package com.task.autoeversecurity.config

import com.task.autoeversecurity.component.BasicAuthUserListener
import com.task.autoeversecurity.util.BasicAuthUsers
import com.task.autoeversecurity.util.Constants.Redis.BASIC_AUTH_USERS_CHANNEL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfiguration(
    private val basicAuthUserListener: BasicAuthUserListener,
) {
    @Bean
    fun redisMessageListener(connectionFactory: RedisConnectionFactory): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        container.addMessageListener(basicAuthUserListener, ChannelTopic(BASIC_AUTH_USERS_CHANNEL))

        return container
    }

    @Bean
    fun basicAuthUsersRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, BasicAuthUsers> {
        val template = RedisTemplate<String, BasicAuthUsers>()

        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = StringRedisSerializer()

        return template
    }
}
