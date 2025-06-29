package com.task.autoeversecurity.client

import com.task.autoeversecurity.dto.api.SendSmsApiRequest
import com.task.autoeversecurity.dto.api.SendSmsApiResponse
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@HttpExchange
interface SmsApiClient {
    @PostExchange("/sms")
    fun sendSms(
        @RequestParam phone: String,
        @RequestBody request: SendSmsApiRequest,
    ): SendSmsApiResponse
}
