package com.taali.domain.repository.message

import com.taali.domain.model.message.Message
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MessageRepository : PanacheRepository<Message> {

    fun findUnreadMessages(conversationId: Long, userId: Long): List<Message> {
        return find(
            "conversation.id = ?1 AND sender.id != ?2 AND isRead = false",
            conversationId, userId
        ).list()
    }

    fun findMessagesByConversation(conversationId: Long): List<Message> {
        return find("conversation.id = ?1 ORDER BY createdAt ASC", conversationId).list()
    }
}