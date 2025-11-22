package com.taali.domain.model.message

import com.taali.domain.model.common.AuditableEntity
import com.taali.domain.model.school.Student
import com.taali.domain.model.user.User
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "conversations")
class Conversation : AuditableEntity() {

    @Column(nullable = false)
    var subject: String = ""

    @Column(nullable = false)
    lateinit var category: String // ABSENCE, ACADEMIC, BEHAVIOR, GENERAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    lateinit var initiator: User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    lateinit var receiver: User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    var student: Student? = null

    @Column(name = "is_closed", nullable = false)
    var isClosed: Boolean = false

    @Column(name = "last_message_at", nullable = false)
    var lastMessageAt: LocalDateTime = LocalDateTime.now()

    // Track unread counts
    @Column(name = "unread_count_initiator", nullable = false)
    var unreadCountInitiator: Int = 0

    @Column(name = "unread_count_receiver", nullable = false)
    var unreadCountReceiver: Int = 0

    // Relationships
    @OneToMany(mappedBy = "conversation", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var messages: MutableList<Message> = mutableListOf()

    // Helper methods
    fun getUnreadCountForUser(userId: Long): Int {
        return when (userId) {
            initiator.id -> unreadCountInitiator
            receiver.id -> unreadCountReceiver
            else -> 0
        }
    }

    fun incrementUnreadCountForUser(userId: Long) {
        when (userId) {
            initiator.id -> unreadCountInitiator++
            receiver.id -> unreadCountReceiver++
        }
    }

    fun resetUnreadCountForUser(userId: Long) {
        when (userId) {
            initiator.id -> unreadCountInitiator = 0
            receiver.id -> unreadCountReceiver = 0
        }
    }

    fun getOtherParticipant(userId: Long): User? {
        return when (userId) {
            initiator.id -> receiver
            receiver.id -> initiator
            else -> null
        }
    }

    companion object : PanacheCompanion<Conversation> {
        // Custom query methods can be added here
        fun findByInitiator(initiatorId: Long): List<Conversation> {
            return list("initiator.id = ?1", initiatorId)
        }

        fun findByReceiver(receiverId: Long): List<Conversation> {
            return list("receiver.id = ?1", receiverId)
        }

        fun findByParticipant(userId: Long): List<Conversation> {
            return list("initiator.id = ?1 OR receiver.id = ?1", userId)
        }

        fun findUnreadByUser(userId: Long): List<Conversation> {
            return list(
                "(initiator.id = ?1 AND unreadCountInitiator > 0) OR (receiver.id = ?1 AND unreadCountReceiver > 0)",
                userId
            )
        }
    }
}