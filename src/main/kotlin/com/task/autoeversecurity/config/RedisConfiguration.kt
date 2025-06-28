package com.task.autoeversecurity.config

import com.task.autoeversecurity.util.BasicAuthUsers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfiguration {
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
