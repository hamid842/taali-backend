package com.taali.domain.repository.message

import com.taali.domain.model.message.Conversation
import com.taali.domain.model.message.Message
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ConversationRepository : PanacheRepository<Conversation> {

    fun findConversationsForUser(userId: Long): List<Conversation> {
        return find(
            "initiator.id = ?1 OR receiver.id = ?1 ORDER BY lastMessageAt DESC",
            userId
        ).list()
    }

    fun findConversationWithMessages(conversationId: Long, userId: Long): Conversation? {
        val conversation = findById(conversationId) ?: return null

        // Verify user has access to this conversation
        if (conversation.initiator.id != userId && conversation.receiver.id != userId) {
            return null
        }

        // Eagerly load messages - use the entityManager from PanacheRepository
        conversation.messages = getEntityManager().createQuery(
            "SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt ASC",
            Message::class.java
        ).setParameter("conversationId", conversationId)
            .resultList

        return conversation
    }

    fun getUnreadConversationsCount(userId: Long): Long {
        return count(
            "(initiator.id = ?1 AND unreadCountInitiator > 0) OR (receiver.id = ?1 AND unreadCountReceiver > 0)",
            userId
        )
    }

    fun findConversationsByCategory(userId: Long, category: String): List<Conversation> {
        return find(
            "(initiator.id = ?1 OR receiver.id = ?1) AND category = ?2 ORDER BY lastMessageAt DESC",
            userId, category
        ).list()
    }
}