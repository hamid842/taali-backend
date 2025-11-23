package com.taali.api.rest.message

import MessageDTO
import com.taali.application.service.message.ConversationService
import com.taali.application.service.message.UserLookupService
import com.taali.api.dto.message.*
import com.taali.api.dto.message.request.CreateConversationRequest
import com.taali.api.dto.message.request.SendMessageRequest
import com.taali.api.dto.shared.ApiResponse
import com.taali.domain.model.user.User
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath

@Path("/conversations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ConversationResource {

    @Inject
    lateinit var conversationService: ConversationService

    @Inject
    lateinit var userLookupService: UserLookupService

    @POST
    @Transactional
    fun createConversation(
        request: CreateConversationRequest, @HeaderParam("X-User-Id") userId: Long
    ): Response {
        return try {
            val initiator = User.findById(userId) ?: return Response.status(Response.Status.UNAUTHORIZED)
                .entity(ApiResponse.error<ConversationDTO>("User not found")).build()

            val conversation = conversationService.createConversation(request, initiator)
            val conversationDTO = conversationService.toConversationDTO(conversation, userId)

            Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Conversation created successfully", conversationDTO)).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error<ConversationDTO>("Failed to create conversation: ${e.message}")).build()
        }
    }

    @POST
    @Path("/{conversationId}/messages")
    @Transactional
    fun sendMessage(
        @RestPath conversationId: Long, request: SendMessageRequest, @HeaderParam("X-User-Id") userId: Long
    ): Response {
        return try {
            val sender = User.findById(userId) ?: return Response.status(Response.Status.UNAUTHORIZED)
                .entity(ApiResponse.error<MessageDTO>("User not found")).build()

            val message = conversationService.sendMessage(conversationId, request, sender)
            val messageDTO = conversationService.toMessageDTO(message)

            Response.ok(ApiResponse.success("Message sent successfully", messageDTO)).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error<MessageDTO>(e.message ?: "Conversation not found")).build()
        } catch (e: SecurityException) {
            Response.status(Response.Status.FORBIDDEN)
                .entity(ApiResponse.error<MessageDTO>(e.message ?: "Access denied")).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error<MessageDTO>("Failed to send message: ${e.message}")).build()
        }
    }

    @GET
    fun getConversations(@HeaderParam("X-User-Id") userId: Long): Response {
        return try {
            val conversations = conversationService.getConversationsForUser(userId)
            val conversationDTOs = conversations.map {
                conversationService.toConversationDTO(it, userId)
            }

            Response.ok(ApiResponse.success("Conversations retrieved successfully", conversationDTOs)).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error<List<ConversationDTO>>("Failed to fetch conversations: ${e.message}")).build()
        }
    }

    @GET
    @Path("/{conversationId}")
    fun getConversation(
        @RestPath conversationId: Long, @HeaderParam("X-User-Id") userId: Long
    ): Response {
        return try {
            val conversation =
                conversationService.getConversationWithMessages(conversationId, userId) ?: return Response.status(
                    Response.Status.NOT_FOUND
                ).entity(ApiResponse.error<ConversationWithMessagesDTO>("Conversation not found")).build()

            val response = conversationService.toConversationWithMessagesDTO(conversation, userId)

            Response.ok(ApiResponse.success("Conversation retrieved successfully", response)).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error<ConversationWithMessagesDTO>("Failed to fetch conversation: ${e.message}"))
                .build()
        }
    }

    @PUT
    @Path("/{conversationId}/read")
    @Transactional
    fun markAsRead(@RestPath conversationId: Long, @HeaderParam("X-User-Id") userId: Long): Response {
        return try {
            conversationService.markConversationAsRead(conversationId, userId)

            Response.ok(ApiResponse.success<Unit>("Conversation marked as read")).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error<Unit>("Failed to mark conversation as read: ${e.message}")).build()
        }
    }

    @PUT
    @Path("/{conversationId}/close")
    @Transactional
    fun closeConversation(@RestPath conversationId: Long, @HeaderParam("X-User-Id") userId: Long): Response {
        return try {
            val success = conversationService.closeConversation(conversationId, userId)
            if (!success) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error<Unit>("Conversation not found")).build()
            }

            Response.ok(ApiResponse.success<Unit>("Conversation closed")).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error<Unit>("Failed to close conversation: ${e.message}")).build()
        }
    }

    @GET
    @Path("/unread/count")
    fun getUnreadCount(@HeaderParam("X-User-Id") userId: Long): Response {
        return try {
            val count = conversationService.getUnreadConversationsCount(userId)

            Response.ok(ApiResponse.success("Unread count retrieved", count)).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error<Long>("Failed to fetch unread count: ${e.message}")).build()
        }
    }
}