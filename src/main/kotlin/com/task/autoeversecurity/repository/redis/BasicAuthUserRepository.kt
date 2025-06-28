package com.task.autoeversecurity.repository.redis

import com.task.autoeversecurity.util.BasicAuthUsers
import com.task.autoeversecurity.util.Constants.BASIC_AUTH_USERS_REDIS_KEY
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class BasicAuthUserRepository(
    private val basicAuthUserRedisTemplate: RedisTemplate<String, BasicAuthUsers>,
) {
    fun getBasicAuthUsers(): BasicAuthUsers {
        return basicAuthUserRedisTemplate.opsForHash<String, String>()
            .entries(BASIC_AUTH_USERS_REDIS_KEY)
    }
}
