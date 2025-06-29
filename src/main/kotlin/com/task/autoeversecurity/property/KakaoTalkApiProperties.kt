package com.task.autoeversecurity.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("api.kakao-talk")
data class KakaoTalkApiProperties(
    val baseUrl: String,
    val basicAuth: BasicAuth,
)
