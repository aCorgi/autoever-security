package com.task.autoeversecurity.repository.redis

import com.task.autoeversecurity.util.BasicAuthUsers
import com.task.autoeversecurity.util.Constants.Redis.BASIC_AUTH_USERS_REDIS_KEY
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.stereotype.Repository

@Repository
class BasicAuthUserRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val inMemoryUserDetailsManager: InMemoryUserDetailsManager,
) {
    fun addAllBasicAuthUsersInUserDetailsManager() {
        getBasicAuthUsers().map { (username, password) ->
            val userDetail = User(username, password, emptyList())

            if (!inMemoryUserDetailsManager.userExists(username)) {
                inMemoryUserDetailsManager.createUser(userDetail)
            }
        }
    }

    fun setAdminInBasicAuthUsers(
        name: String,
        password: String,
    ) {
        redisTemplate.opsForHash<String, String>()
            .putIfAbsent(BASIC_AUTH_USERS_REDIS_KEY, name, password)
    }

    fun getBasicAuthUsers(): BasicAuthUsers {
        return redisTemplate.opsForHash<String, String>()
            .entries(BASIC_AUTH_USERS_REDIS_KEY)
    }
}
