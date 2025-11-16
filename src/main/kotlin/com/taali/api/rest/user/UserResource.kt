package com.taali.api.rest.user

import com.taali.application.service.user.UserService
import com.taali.domain.enum.UserRole
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestQuery

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "User management APIs")
class UserResource {

    @Inject
    lateinit var userService: UserService

    @GET
    @RolesAllowed("OWNER", "SCHOOL_MANAGER", "SCHOOL_ADMIN")
    @Operation(summary = "Get users with pagination and filtering")
    fun getUsers(
        @Context securityContext: SecurityContext,
        @RestQuery("page") @DefaultValue("1") page: Int,
        @RestQuery("size") @DefaultValue("10") size: Int,
        @RestQuery("search") search: String?,
        @RestQuery("role") role: UserRole?
    ): Response {
        try {
            val currentUserEmail = securityContext.userPrincipal.name
            val response = userService.getUsers(currentUserEmail, page, size, search, role)
            return Response.ok(response).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch users: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/{userId}")
    @RolesAllowed("OWNER", "SCHOOL_MANAGER", "SCHOOL_ADMIN")
    @Operation(summary = "Get user by ID")
    fun getUserById(
        @Context securityContext: SecurityContext,
        @PathParam("userId") userId: String
    ): Response {
        try {
            val currentUserEmail = securityContext.userPrincipal.name
            val user = userService.getUserById(currentUserEmail, userId)
            return Response.ok(user).build()
        } catch (e: SecurityException) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity(mapOf("error" to "Access denied"))
                .build()
        } catch (e: NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to "User not found"))
                .build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch user: ${e.message}"))
                .build()
        }
    }

    @PUT
    @Path("/{userId}/status")
    @RolesAllowed("OWNER", "SCHOOL_MANAGER", "SCHOOL_ADMIN")
    @Operation(summary = "Update user status")
    fun updateUserStatus(
        @Context securityContext: SecurityContext,
        @PathParam("userId") userId: String,
        updateRequest: Map<String, Boolean>
    ): Response {
        try {
            val currentUserEmail = securityContext.userPrincipal.name
            val isActive = updateRequest["isActive"] ?: throw BadRequestException("isActive field is required")

            val user = userService.updateUserStatus(currentUserEmail, userId, isActive)
            return Response.ok(user).build()
        } catch (e: SecurityException) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity(mapOf("error" to "Access denied"))
                .build()
        } catch (e: BadRequestException) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to update user: ${e.message}"))
                .build()
        }
    }
}