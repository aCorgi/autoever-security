package com.task.autoeversecurity.dto.message

data class SendSmsMessageDto(
    val phone: String,
    val message: String,
)
