package com.taali.api.rest.user

import com.taali.api.dto.user.UpdateUserSettingsRequest
import com.taali.domain.model.user.User
import com.taali.domain.service.shared.TranslationService
import com.taali.domain.service.user.UserSettingsService
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.Locale

@Path("/api/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class UserSettingsResource {

    @Inject
    lateinit var userSettingsService: UserSettingsService

    @Inject
    lateinit var translationService: TranslationService

    @Inject
    lateinit var jwt: JsonWebToken

    @Context
    lateinit var securityContext: SecurityContext

    @GET
    @Path("/settings")
    @RolesAllowed("ADMIN", "SUPERVISOR", "TEACHER", "STUDENT", "PARENT", "CANTEEN_OPERATOR", "FINANCE_TEAM")
    fun getUserSettings(): Response {
        try {
            val user = getCurrentUser()
            val settings = userSettingsService.getUserSettings(user)
            return Response.ok(settings).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch user settings: ${e.message}"))
                .build()
        }
    }

    @PUT
    @Path("/settings")
    @RolesAllowed("ADMIN", "SUPERVISOR", "TEACHER", "STUDENT", "PARENT", "CANTEEN_OPERATOR", "FINANCE_TEAM")
    fun updateUserSettings(request: UpdateUserSettingsRequest): Response {
        try {
            val user = getCurrentUser()
            val updatedSettings = userSettingsService.updateUserSettings(user, request)
            return Response.ok(updatedSettings).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to update user settings: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/menu")
    @RolesAllowed("ADMIN", "SUPERVISOR", "TEACHER", "STUDENT", "PARENT", "CANTEEN_OPERATOR", "FINANCE_TEAM")
    fun getUserMenu(): Response {
        try {
            val user = getCurrentUser()
            val menu = userSettingsService.getUserMenu(user)
            return Response.ok(menu).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch user menu: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/translations")
    @RolesAllowed("ADMIN", "SUPERVISOR", "TEACHER", "STUDENT", "PARENT", "CANTEEN_OPERATOR", "FINANCE_TEAM")
    fun getTranslations(@QueryParam("locale") locale: Locale?): Response {
        try {
            val user = getCurrentUser()
            val userSettings = userSettingsService.getUserSettings(user)
            val actualLocale = locale ?: userSettings.preferredLanguage

            val translations = translationService.getMessagesForLocale(actualLocale)
            return Response.ok(translations).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch translations: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/preferences")
    @RolesAllowed("ADMIN", "SUPERVISOR", "TEACHER", "STUDENT", "PARENT", "CANTEEN_OPERATOR", "FINANCE_TEAM")
    fun getUserPreferences(): Response {
        try {
            val user = getCurrentUser()
            val settings = userSettingsService.getUserSettings(user)
            val menu = userSettingsService.getUserMenu(user)

            val preferences = mapOf(
                "settings" to settings,
                "menu" to menu,
                "user" to mapOf(
                    "id" to user.id,
                    "firstName" to user.firstName,
                    "lastName" to user.lastName,
                    "email" to user.email,
                    "role" to user.role.name
                )
            )

            return Response.ok(preferences).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch user preferences: ${e.message}"))
                .build()
        }
    }

    @POST
    @Path("/settings/reset")
    @RolesAllowed("ADMIN", "SUPERVISOR", "TEACHER", "STUDENT", "PARENT", "CANTEEN_OPERATOR", "FINANCE_TEAM")
    fun resetUserSettings(): Response {
        try {
            val user = getCurrentUser()
            val defaultSettings = userSettingsService.resetToDefaultSettings(user)
            return Response.ok(defaultSettings).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to reset user settings: ${e.message}"))
                .build()
        }
    }

    private fun getCurrentUser(): User {
        // Extract user ID from JWT token
        val userId = jwt.getClaim<Long>("userId")
            ?: throw NotAuthorizedException("User ID not found in token")

        // Find user by ID
        val user = User.findById(userId)
            ?: throw NotFoundException("User not found with ID: $userId")

        return user
    }
}