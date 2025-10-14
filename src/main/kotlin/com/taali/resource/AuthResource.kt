package com.taali.resource

import com.taali.dto.*
import com.taali.entity.User
import com.taali.entity.RefreshToken
import com.taali.service.*
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.time.LocalDateTime
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "User authentication and registration")
class AuthResource {

    @Inject
    lateinit var jwtService: JwtService

    @Inject
    lateinit var passwordService: PasswordService

    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var localizationService: LocalizationService

    @Context
    lateinit var httpHeaders: HttpHeaders

    // Valid roles for registration
    private val validRoles = listOf("STUDENT", "TEACHER", "ADMIN", "PARENT")

    @POST
    @Path("/register")
    @Transactional
    @Operation(summary = "Register a new user")
    fun register(@Valid request: RegisterRequest): Response {
        return try {
            // Validate role
            if (!validRoles.contains(request.role.uppercase())) {
                val rolesString = validRoles.joinToString(", ")
                val message = localizationService.getMessage("auth.register.invalid_role", rolesString)
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(message))
                    .build()
            }

            // Check if user already exists
            if (User.existsByEmail(request.email)) {
                val message = localizationService.getMessage("auth.register.email_exists")
                return Response.status(Response.Status.CONFLICT)
                    .entity(ApiResponse.error(message))
                    .build()
            }

            // Create new user
            val user = User().apply {
                email = request.email.lowercase().trim()
                password = passwordService.hashPassword(request.password)
                firstName = request.firstName
                lastName = request.lastName
                phone = request.phone
                role = request.role.uppercase()
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
                isActive = true
                emailVerified = false
            }

            user.persist()

            // Generate tokens
            val accessToken = jwtService.generateToken(user)
            val refreshToken = authService.createRefreshToken(user)

            val authResponse = AuthResponse(
                accessToken = accessToken,
                refreshToken = refreshToken.token,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                phone = user.phone,
                role = user.role,
                message = localizationService.getMessage("auth.register.success")
            )

            Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(localizationService.getMessage("auth.register.success"), authResponse))
                .build()

        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error(localizationService.getMessage("auth.register.error") + ": " + e.message))
                .build()
        }
    }

    @POST
    @Path("/login")
    @Operation(summary = "Login user")
    fun login(@Valid request: AuthRequest): Response {
        return try {
            // Find user by email
            val user = User.findByEmail(request.email.lowercase().trim())
            if (user == null) {
                val message = localizationService.getMessage("auth.login.invalid_credentials")
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error(message))
                    .build()
            }

            // Check if user is active
            if (!user.isActive) {
                val message = localizationService.getMessage("auth.login.account_inactive")
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error(message))
                    .build()
            }

            // Verify password
            if (!passwordService.checkPassword(request.password, user.password)) {
                val message = localizationService.getMessage("auth.login.invalid_credentials")
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error(message))
                    .build()
            }

            // Update last login
            user.lastLogin = LocalDateTime.now()
            user.persist()

            // Generate tokens
            val accessToken = jwtService.generateToken(user)
            val refreshToken = authService.createRefreshToken(user)

            val authResponse = AuthResponse(
                accessToken = accessToken,
                refreshToken = refreshToken.token,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                phone = user.phone,
                role = user.role,
                message = localizationizationService.getMessage("auth.login.success")
            )

            Response.ok(ApiResponse.success(localizationService.getMessage("auth.login.success"), authResponse)).build()

        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error(localizationService.getMessage("auth.login.error") + ": " + e.message))
                .build()
        }
    }

    // Add other endpoints (refresh, logout) as needed...
}