package com.taali.api.dto.message.response

import java.time.LocalDateTime

data class MessageResponse(
    val id: Long,
    val content: String,
    val senderId: Long,
    val senderName: String,
    val conversationId: Long,
    val isRead: Boolean,
    val readAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val messageType: String
)