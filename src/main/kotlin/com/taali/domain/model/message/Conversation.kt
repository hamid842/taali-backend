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

    // Add these computed properties for easier DTO conversion
    val unreadCount: Int
        get() = unreadCountInitiator + unreadCountReceiver

    val messageCount: Int
        get() = messages.size

    val lastMessage: Message?
        get() = messages.maxByOrNull { it.createdAt }

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

    fun getOtherParticipant(userId: Long?): User? {
        return when (userId) {
            initiator.id -> receiver
            receiver.id -> initiator
            else -> null
        }
    }

    // Add this method to safely initialize lazy properties for DTO conversion
    fun initializeForDTO() {
        // Force initialization of lazy properties that will be used in DTOs
        try {
            // Access basic properties to ensure they're loaded
            initiator.id
            initiator.firstName
            receiver.id
            receiver.firstName
            student?.id
            student?.getFullName()
            // Access messages collection to ensure it's initialized
            messages.size
            lastMessage?.content
        } catch (e: Exception) {
            // Log the exception but don't throw - this is just for DTO preparation
            println("Warning: Failed to initialize some lazy properties for DTO: ${e.message}")
        }
    }

    companion object : PanacheCompanion<Conversation> {
        // Custom query methods with proper fetching for common use cases
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

        // Add a method that eagerly fetches conversations with necessary relations
        fun findByIdWithAssociations(id: Long): Conversation? {
            return find(
                """
                SELECT c FROM Conversation c 
                LEFT JOIN FETCH c.initiator
                LEFT JOIN FETCH c.receiver  
                LEFT JOIN FETCH c.student
                LEFT JOIN FETCH c.messages
                WHERE c.id = ?1
                """, id
            ).firstResult()
        }

        fun findByParticipantWithAssociations(userId: Long): List<Conversation> {
            return list(
                """
                SELECT DISTINCT c FROM Conversation c 
                LEFT JOIN FETCH c.initiator
                LEFT JOIN FETCH c.receiver
                LEFT JOIN FETCH c.student
                WHERE c.initiator.id = ?1 OR c.receiver.id = ?1
                ORDER BY c.lastMessageAt DESC
                """, userId
            )
        }
    }
}