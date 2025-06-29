package com.task.autoeversecurity.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("api.sms")
data class SmsApiProperties(
    val baseUrl: String,
    val basicAuth: BasicAuth,
)
