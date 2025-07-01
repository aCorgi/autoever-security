package com.task.autoeversecurity.dto.api

import com.task.autoeversecurity.dto.message.SendKakaoTalkMessageDto

data class SendKakaoTalkApiRequest(
    val phone: String,
    val message: String,
) {
    constructor(messageDto: SendKakaoTalkMessageDto) : this(
        phone = messageDto.phone,
        message = messageDto.content,
    )
}
