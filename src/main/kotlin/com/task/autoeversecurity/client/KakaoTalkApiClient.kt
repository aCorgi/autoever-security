package com.task.autoeversecurity.client

import com.task.autoeversecurity.dto.api.SendKakaoTalkApiRequest
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@HttpExchange
interface KakaoTalkApiClient {
    @PostExchange("/kakaotalk-messages")
    fun sendKakaoTalkMessage(
        @RequestBody request: SendKakaoTalkApiRequest,
    )
}
