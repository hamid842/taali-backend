package com.taali.api.dto.message.request

data class CreateConversationRequest(
    val subject: String,
    val content: String,
    val category: String,
    val receiverId: Long,
    val studentId: Long? = null
)