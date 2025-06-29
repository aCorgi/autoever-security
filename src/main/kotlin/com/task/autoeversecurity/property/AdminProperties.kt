package com.task.autoeversecurity.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("admin")
data class AdminProperties(
    val basicAuth: BasicAuth,
)
