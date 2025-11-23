package com.taali.api.rest.notification

import com.taali.api.dto.shared.ApiResponse
import com.taali.application.service.message.ConversationService
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

data class RegisterDeviceRequest(
    val expoPushToken: String,
    val deviceType: String? = null
)

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class NotificationResource {

    @Inject
    lateinit var conversationService: ConversationService

    @POST
    @Path("/register-device")
    fun registerDevice(
        @HeaderParam("X-User-Id") userId: Long,
        request: RegisterDeviceRequest
    ): Response {
        return try {
            conversationService.registerDeviceToken(userId, request.expoPushToken, request.deviceType)
            Response.ok(ApiResponse.success<Unit>("Device registered successfully")).build()
        } catch (e: Exception) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error<Unit>("Failed to register device: ${e.message}")).build()
        }
    }

    @POST
    @Path("/unregister-device")
    fun unregisterDevice(
        @HeaderParam("X-User-Id") userId: Long,
        @QueryParam("token") token: String
    ): Response {
        // Implement if needed - you can deactivate tokens
        return Response.ok(ApiResponse.success<Unit>("Device unregistered")).build()
    }
}