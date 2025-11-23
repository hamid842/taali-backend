package com.taali.api.dto.message.request

data class CreateConversationRequest(
    val receiverId: Long,
    val studentId: Long? = null,
    val subject: String,
    val category: String = "GENERAL",
    val content: String
)