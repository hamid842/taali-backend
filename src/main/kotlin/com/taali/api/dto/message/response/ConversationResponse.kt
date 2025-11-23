package com.taali.api.dto.message.response

import java.time.LocalDateTime

data class ConversationResponse(
    val id: Long,
    val subject: String,
    val category: String,
    val initiatorId: Long,
    val initiatorName: String,
    val receiverId: Long,
    val receiverName: String,
    val studentId: Long?,
    val studentName: String?,
    val isClosed: Boolean,
    val lastMessageAt: LocalDateTime,
    val unreadCount: Int,
    val lastMessagePreview: String?,
    val participantNames: List<String>
)