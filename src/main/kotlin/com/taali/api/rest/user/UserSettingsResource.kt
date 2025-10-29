package com.taali.api.rest.user

import com.taali.api.dto.user.UpdateUserSettingsRequest
import com.taali.domain.model.user.User
import com.taali.shared.TranslationService
import com.taali.application.service.user.UserSettingsService
import com.taali.shared.RequestContext
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

    @Inject
    lateinit var requestContext: RequestContext

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
    fun getTranslations(@QueryParam("locale") locale: String?): Response {
        try {
            val user = getCurrentUser()
            val userSettings = userSettingsService.getUserSettings(user)

            // Priority: Query param -> User settings -> Request context
            val actualLocale = locale ?: userSettings.preferredLanguage ?: requestContext.language

            // Get all translations by calling translate() for each key
            val translations = getAllTranslations(actualLocale as String)
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

    // Helper method to get all translations using translate() method
    private fun getAllTranslations(language: String): Map<String, String> {
        val allKeys = listOf(
            // Menu keys
            "menu_dashboard",
            "menu_school_management",
            "menu_create_school",
            "menu_list_schools",
            "menu_user_management",
            "menu_create_user",
            "menu_list_users",
            "menu_finance",
            "menu_teacher_management",
            "menu_class_management",
            "menu_student_management",
            "menu_parent_management",
            "menu_my_classes",
            "menu_my_students",
            "menu_attendance",
            "menu_assignments",
            "menu_my_profile",
            "menu_my_grades",
            "menu_my_children",
            "menu_children_grades",
            "menu_children_attendance",
            "menu_payments",
            "menu_food_menu",
            "menu_orders",
            "menu_inventory",
            "menu_financial_reports",
            "menu_payment_management",
            "menu_invoices",

            // Toast keys
            "toast_otp_sent",
            "toast_register_success",
            "toast_register_failed",
            "toast_login_success",
            "toast_login_failed",
            "toast_redirecting_dashboard",
            "toast_success",
            "toast_error",
            "toast_warning",
            "toast_info",
            "toast_loading",
            "toast_otp_verified",
            "toast_otp_invalid",
            "toast_otp_expired",
            "toast_profile_updated",
            "toast_password_changed",

            // Validation keys
            "validation_first_name_required",
            "validation_first_name_size",
            "validation_first_name_pattern_en",
            "validation_first_name_pattern_fa",
            "validation_last_name_required",
            "validation_last_name_size",
            "validation_last_name_pattern_en",
            "validation_last_name_pattern_fa",
            "validation_email_required",
            "validation_email_format",
            "validation_email_exists",
            "validation_phone_required",
            "validation_phone_format",
            "validation_password_required",
            "validation_password_size",
            "validation_password_pattern",
            "validation_role_required",
            "validation_userId_required",
            "validation_otp_required",
            "validation_otp_size",

            // User and Auth keys
            "user_email_exists",
            "user_phone_exists",
            "user_weak_password",
            "user_not_found",
            "user_inactive",
            "auth_registration_success",
            "auth_registration_failed",
            "auth_otp_sent",
            "auth_otp_resent",
            "auth_invalid_otp",
            "auth_invalid_session",
            "auth_verification_failed",
            "auth_resend_failed",
            "auth_login_success",
            "auth_login_failed",
            "auth_invalid_credentials",
            "auth_account_locked",
            "auth_unauthorized",

            // Email OTP keys
            "email_otp_subject",
            "email_otp_greeting",
            "email_otp_message",
            "email_otp_expiry",
            "email_otp_ignore"
        )

        val translations = mutableMapOf<String, String>()

        allKeys.forEach { key ->
            translations[key] = translationService.translate(key, language)
        }

        return translations
    }
}