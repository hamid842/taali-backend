package com.taali.api.dto.message

import com.taali.domain.enum.UserRole
import java.time.LocalDateTime

data class ConversationDTO(
    val id: Long?,
    val subject: String,
    val category: String,
    val initiator: UserDTO,
    val receiver: UserDTO,
    val student: StudentDTO?,
    val isClosed: Boolean,
    val lastMessageAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val unreadCount: Int,
    val lastMessage: MessagePreviewDTO?,
    val messageCount: Int
) {
    data class UserDTO(
        val id: Long?,
        val firstName: String,
        val lastName: String,
        val email: String,
        val role: UserRole
    )

    data class StudentDTO(
        val id: Long?,
        val firstName: String?,
        val lastName: String?,
        val grade: String?
    )

    data class MessagePreviewDTO(
        val content: String,
        val senderName: String,
        val createdAt: LocalDateTime
    )
}