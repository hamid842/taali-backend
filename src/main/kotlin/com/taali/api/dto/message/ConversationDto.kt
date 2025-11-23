package com.taali.api.dto.message

import com.taali.domain.enum.UserRole
import com.taali.domain.model.message.Conversation
import com.taali.domain.model.user.User
import com.taali.domain.model.school.Student
import com.taali.domain.model.message.Message
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
    companion object {
        fun fromEntity(conversation: Conversation): ConversationDTO {
            return ConversationDTO(
                id = conversation.id,
                subject = conversation.subject,
                category = conversation.category,
                initiator = UserDTO.fromEntity(conversation.initiator),
                receiver = UserDTO.fromEntity(conversation.receiver),
                student = conversation.student?.let { StudentDTO.fromEntity(it) },
                isClosed = conversation.isClosed,
                lastMessageAt = conversation.lastMessageAt,
                createdAt = conversation.createdAt,
                unreadCount = conversation.unreadCount,
                lastMessage = conversation.lastMessage?.let { MessagePreviewDTO.fromEntity(it) },
                messageCount = conversation.messageCount
            )
        }
    }

    data class UserDTO(
        val id: Long?,
        val firstName: String,
        val lastName: String,
        val email: String,
        val role: UserRole
    ) {
        companion object {
            fun fromEntity(user: User): UserDTO {
                // Safely handle potentially lazy-loaded properties
                val firstName = try {
                    user.firstName
                } catch (e: Exception) {
                    ""
                }

                val lastName = try {
                    user.lastName
                } catch (e: Exception) {
                    ""
                }

                val email = try {
                    user.email
                } catch (e: Exception) {
                    ""
                }

                return UserDTO(
                    id = user.id,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    role = user.role
                )
            }
        }
    }

    data class StudentDTO(
        val id: Long?,
        val firstName: String?,
        val lastName: String?,
        val grade: String?
    ) {
        companion object {
            fun fromEntity(student: Student): StudentDTO {
                // Safely handle potentially lazy-loaded properties
                val firstName = try {
                    student.user?.firstName
                } catch (e: Exception) {
                    null
                }

                val lastName = try {
                    student.user?.lastName
                } catch (e: Exception) {
                    null
                }

                val grade = try {
                    student.gradeLevel
                } catch (e: Exception) {
                    null
                }

                return StudentDTO(
                    id = student.id,
                    firstName = firstName,
                    lastName = lastName,
                    grade = grade
                )
            }
        }
    }

    data class MessagePreviewDTO(
        val content: String,
        val senderName: String,
        val createdAt: LocalDateTime
    ) {
        companion object {
            fun fromEntity(message: Message): MessagePreviewDTO {
                // Safely handle potentially lazy-loaded properties
                val content = try {
                    message.content
                } catch (e: Exception) {
                    ""
                }

                val senderName = try {
                    "${message.sender.firstName} ${message.sender.lastName}"
                } catch (e: Exception) {
                    "Unknown"
                }

                val createdAt = try {
                    message.createdAt
                } catch (e: Exception) {
                    LocalDateTime.now()
                }

                return MessagePreviewDTO(
                    content = content,
                    senderName = senderName,
                    createdAt = createdAt
                )
            }
        }
    }
}