package com.task.autoeversecurity.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("aes")
data class Aes256Properties(
    val algorithm: String,
    val ivSize: Int,
    val secretKey: String,
)
