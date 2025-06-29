package com.task.autoeversecurity.dto.api

data class SendSmsApiRequest(
    val message: String,
)

data class SendSmsApiResponse(
    val result: SmsResultCode,
) {
    enum class SmsResultCode {
        OK,
    }
}
