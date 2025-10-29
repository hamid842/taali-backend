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
            "menu.dashboard",
            "menu.schoolManagement",
            "menu.createSchool",
            "menu.listSchools",
            "menu.userManagement",
            "menu.createUser",
            "menu.listUsers",
            "menu.finance",
            "menu.teacherManagement",
            "menu.classManagement",
            "menu.studentManagement",
            "menu.parentManagement",
            "menu.myClasses",
            "menu.myStudents",
            "menu.attendance",
            "menu.assignments",
            "menu.myProfile",
            "menu.myGrades",
            "menu.myChildren",
            "menu.childrenGrades",
            "menu.childrenAttendance",
            "menu.payments",
            "menu.foodMenu",
            "menu.orders",
            "menu.inventory",
            "menu.financialReports",
            "menu.paymentManagement",
            "menu.invoices",

            // Toast keys
            "toast.otpSent",
            "toast.registerSuccess",
            "toast.registerFailed",
            "toast.loginSuccess",
            "toast.loginFailed",
            "toast.redirectingDashboard",
            "toast.success",
            "toast.error",
            "toast.warning",
            "toast.info",
            "toast.loading",
            "toast.otpVerified",
            "toast.otpInvalid",
            "toast.otpExpired",
            "toast.profileUpdated",
            "toast.passwordChanged",

            // Validation keys
            "validation.firstName.required",
            "validation.firstName.size",
            "validation.firstName.pattern.en",
            "validation.firstName.pattern.fa",
            "validation.lastName.required",
            "validation.lastName.size",
            "validation.lastName.pattern.en",
            "validation.lastName.pattern.fa",
            "validation.email.required",
            "validation.email.format",
            "validation.email.exists",
            "validation.phone.required",
            "validation.phone.format",
            "validation.password.required",
            "validation.password.size",
            "validation.password.pattern",
            "validation.role.required",
            "validation.userId.required",
            "validation.otp.required",
            "validation.otp.size",

            // User and Auth keys
            "user.email_exists",
            "user.phone_exists",
            "user.weak_password",
            "user.not_found",
            "user.inactive",
            "auth.registration_success",
            "auth.registration_failed",
            "auth.otp_sent",
            "auth.otp_resent",
            "auth.invalid_otp",
            "auth.invalid_session",
            "auth.verification_failed",
            "auth.resend_failed",
            "auth.login_success",
            "auth.login_failed",
            "auth.invalid_credentials",
            "auth.account_locked",
            "auth.unauthorized",

            // Email OTP keys
            "email.otp.subject",
            "email.otp.greeting",
            "email.otp.message",
            "email.otp.expiry",
            "email.otp.ignore"
        )

        val translations = mutableMapOf<String, String>()

        allKeys.forEach { key ->
            translations[key] = translationService.translate(key, language)
        }

        return translations
    }
}