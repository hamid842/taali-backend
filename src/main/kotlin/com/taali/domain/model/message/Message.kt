package com.taali.domain.model.message

import com.taali.domain.model.common.AuditableEntity
import com.taali.domain.model.user.User
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "messages")
class Message : AuditableEntity() {

    @Column(columnDefinition = "TEXT", nullable = false)
    lateinit var content: String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    lateinit var sender: User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    lateinit var conversation: Conversation

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false

    @Column(name = "read_at")
    var readAt: LocalDateTime? = null

    // Business logic methods
    fun markAsRead() {
        if (!isRead) {
            isRead = true
            readAt = LocalDateTime.now()
        }
    }

    fun isFromUser(userId: Long): Boolean {
        return sender.id == userId
    }

    // Override toString for better logging/debugging
    override fun toString(): String {
        return "Message(id=$id, content='${content.take(50)}...', sender=${sender.id}, conversation=${conversation.id}, isRead=$isRead)"
    }

    companion object : PanacheCompanion<Message> {
        // Custom query methods
        fun findByConversation(conversationId: Long): List<Message> {
            return list("conversation.id = ?1 ORDER BY createdAt ASC", conversationId)
        }

        fun findUnreadByUser(userId: Long): List<Message> {
            return list("conversation.receiver.id = ?1 AND isRead = false", userId)
        }

        fun findBySender(senderId: Long): List<Message> {
            return list("sender.id = ?1", senderId)
        }
    }
}