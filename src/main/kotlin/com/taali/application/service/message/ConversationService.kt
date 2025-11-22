package com.taali.application.service.message;

import MessageDTO
import com.taali.domain.model.message.Conversation
import com.taali.domain.model.message.Message
import com.taali.api.dto.message.*
import com.taali.api.dto.message.request.CreateConversationRequest
import com.taali.api.dto.message.request.SendMessageRequest
import com.taali.domain.model.school.Student
import com.taali.domain.model.user.User
import com.taali.domain.repository.message.ConversationRepository
import com.taali.domain.repository.message.MessageRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@ApplicationScoped
class ConversationService {

    @Inject
    lateinit var conversationRepository: ConversationRepository

    @Inject
    lateinit var messageRepository: MessageRepository

    @Transactional
    fun createConversation(request: CreateConversationRequest, initiator: User): Conversation {
        val receiver = User.findById(request.receiverId) ?: throw IllegalArgumentException("Receiver not found")

        val conversation = Conversation().apply {
            subject = request.subject
            category = request.category
            this.initiator = initiator
            this.receiver = receiver
            createdAt = LocalDateTime.now()
            lastMessageAt = LocalDateTime.now()

            request.studentId?.let { studentId ->
                student = Student.findById(studentId)
            }
        }

        conversation.persist()

        // Create the first message
        val message = Message().apply {
            content = request.content
            sender = initiator
            this.conversation = conversation
            createdAt = LocalDateTime.now()
        }
        message.persist()

        // Add message to conversation and set initial unread count
        conversation.messages.add(message)
        conversation.unreadCountReceiver = 1

        return conversation
    }

    @Transactional
    fun sendMessage(conversationId: Long, request: SendMessageRequest, sender: User): Message {
        val conversation =
            Conversation.findById(conversationId) ?: throw IllegalArgumentException("Conversation not found")

        if (conversation.isClosed) {
            throw IllegalStateException("Cannot send message to closed conversation")
        }

        // Verify sender is a participant in the conversation
        if (sender.id != conversation.initiator.id && sender.id != conversation.receiver.id) {
            throw SecurityException("User is not a participant in this conversation")
        }

        val message = Message().apply {
            content = request.content
            this.sender = sender
            this.conversation = conversation
            createdAt = LocalDateTime.now()
        }
        message.persist()

        return message
    }

    fun getConversationsForUser(userId: Long): List<Conversation> {
        return conversationRepository.findConversationsForUser(userId)
    }

    fun getConversationWithMessages(conversationId: Long, userId: Long): Conversation? {
        return conversationRepository.findConversationWithMessages(conversationId, userId)
    }

    @Transactional
    fun markConversationAsRead(conversationId: Long, userId: Long) {
        val conversation = Conversation.findById(conversationId) ?: return

        // Verify user has access to this conversation
        if (conversation.initiator.id != userId && conversation.receiver.id != userId) {
            return
        }

        // Mark all unread messages as read
        val unreadMessages = messageRepository.findUnreadMessages(conversationId, userId)
        unreadMessages.forEach { message ->
            message.isRead = true
            message.readAt = LocalDateTime.now()
        }

        // Reset unread count for this user
        conversation.resetUnreadCountForUser(userId)
    }

    @Transactional
    fun closeConversation(conversationId: Long, userId: Long): Boolean {
        val conversation = Conversation.findById(conversationId) ?: return false

        // Verify user has access to this conversation
        if (conversation.initiator.id != userId && conversation.receiver.id != userId) {
            return false
        }

        conversation.isClosed = true
        return true
    }

    fun getUnreadConversationsCount(userId: Long): Long {
        return conversationRepository.getUnreadConversationsCount(userId)
    }

    // DTO conversion methods
    fun toConversationDTO(conversation: Conversation, currentUserId: Long): ConversationDTO {
        val lastMessage = conversation.messages.lastOrNull()

        return ConversationDTO(
            id = conversation.id,
            subject = conversation.subject,
            category = conversation.category,
            initiator = toUserDTO(conversation.initiator),
            receiver = toUserDTO(conversation.receiver),
            student = conversation.student?.let { toStudentDTO(it) },
            isClosed = conversation.isClosed,
            lastMessageAt = conversation.lastMessageAt,
            createdAt = conversation.createdAt,
            unreadCount = conversation.getUnreadCountForUser(currentUserId),
            lastMessage = lastMessage?.let {
                ConversationDTO.MessagePreviewDTO(
                    content = it.content.take(100) + if (it.content.length > 100) "..." else "",
                    senderName = "${it.sender.firstName} ${it.sender.lastName}",
                    createdAt = it.createdAt
                )
            },
            messageCount = conversation.messages.size
        )
    }

    fun toMessageDTO(message: Message): MessageDTO {
        return MessageDTO(
            id = message.id,
            content = message.content,
            sender = toUserDTO(message.sender),
            conversationId = message.conversation.id,
            isRead = message.isRead,
            createdAt = message.createdAt,
            readAt = message.readAt
        )
    }

    fun toConversationWithMessagesDTO(conversation: Conversation, currentUserId: Long): ConversationWithMessagesDTO {
        return ConversationWithMessagesDTO(
            conversation = toConversationDTO(conversation, currentUserId),
            messages = conversation.messages.map { toMessageDTO(it) })
    }

    private fun toUserDTO(user: User): ConversationDTO.UserDTO {
        return ConversationDTO.UserDTO(
            id = user.id, firstName = user.firstName, lastName = user.lastName, email = user.email, role = user.role
        )
    }

    private fun toStudentDTO(student: Student): ConversationDTO.StudentDTO {
        return ConversationDTO.StudentDTO(
            id = student.id, firstName = student.user?.firstName, lastName = student.user?.lastName, grade = student.gradeLevel
        )
    }
}