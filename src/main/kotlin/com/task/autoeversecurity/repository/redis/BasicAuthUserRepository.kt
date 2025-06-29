package com.task.autoeversecurity.repository.redis

import com.task.autoeversecurity.util.BasicAuthUsers
import com.task.autoeversecurity.util.Constants.Redis.BASIC_AUTH_USERS_REDIS_KEY
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class BasicAuthUserRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun setAdminInBasicAuthUsers(
        name: String,
        password: String,
    ) {
        redisTemplate.opsForHash<String, String>()
            .put(BASIC_AUTH_USERS_REDIS_KEY, name, password)
    }

    fun getBasicAuthUsersByNameOrNull(username: String): String? {
        return getBasicAuthUsers().get(username)
    }

    private fun getBasicAuthUsers(): BasicAuthUsers {
        return redisTemplate.opsForHash<String, String>()
            .entries(BASIC_AUTH_USERS_REDIS_KEY)
    }
}
