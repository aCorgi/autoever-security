package com.task.autoeversecurity.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("basic-auth")
data class BasicAuthProperties(
    val admin: BasicAuthAdmin,
) {
    data class BasicAuthAdmin(
        val username: String,
        val password: String,
    )
}
