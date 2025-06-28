package com.task.autoeversecurity.repository.redis

import com.task.autoeversecurity.util.BasicAuthUsers
import com.task.autoeversecurity.util.Constants.Redis.BASIC_AUTH_USERS_REDIS_KEY
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.stereotype.Repository

@Repository
class BasicAuthUserRepository(
    private val basicAuthUserRedisTemplate: RedisTemplate<String, BasicAuthUsers>,
    private val userDetailsManager: InMemoryUserDetailsManager,
) : MessageListener {
    override fun onMessage(
        message: Message,
        pattern: ByteArray?,
    ) {
        getBasicAuthUsers().map { (username, password) ->
            val userDetail = User(username, password, emptyList())

            if (!userDetailsManager.userExists(username)) {
                userDetailsManager.createUser(userDetail)
            }
        }
    }

    fun setAdminInBasicAuthUsers(
        name: String,
        password: String,
    ) {
        basicAuthUserRedisTemplate.opsForHash<String, String>()
            .putIfAbsent(BASIC_AUTH_USERS_REDIS_KEY, name, password)
    }

    fun getBasicAuthUsers(): BasicAuthUsers {
        return basicAuthUserRedisTemplate.opsForHash<String, String>()
            .entries(BASIC_AUTH_USERS_REDIS_KEY)
    }
}
