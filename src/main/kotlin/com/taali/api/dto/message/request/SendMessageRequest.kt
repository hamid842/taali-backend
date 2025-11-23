package com.taali.api.dto.message.request

data class SendMessageRequest(
    val conversationId: Long? = null, // Null for new conversation
    val receiverId: Long? = null, // Required for new conversation
    val studentId: Long? = null, // Optional: link message to specific student
    val subject: String? = null, // Required for new conversation
    val category: String = "GENERAL", // ABSENCE, ACADEMIC, BEHAVIOR, GENERAL, SCHOOL_ANNOUNCEMENT
    val content: String,
    val messageType: String = "TEXT" // TEXT, IMAGE, FILE, SYSTEM
)