package com.task.autoeversecurity.component

import com.task.autoeversecurity.repository.redis.BasicAuthUserRepository
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
class BasicAuthUserListener(
    private val basicAuthUserRepository: BasicAuthUserRepository,
) : MessageListener {
    override fun onMessage(
        message: Message,
        pattern: ByteArray?,
    ) {
        basicAuthUserRepository.addAllBasicAuthUsersInUserDetailsManager()
    }
}
