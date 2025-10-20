package com.taali.service

import com.taali.dto.otp.OtpVerificationRequest
import com.taali.dto.otp.ResendOtpRequest
import com.taali.dto.register.RegisterRequest
import com.taali.dto.register.RegisterResponse
import com.taali.entity.RefreshToken
import com.taali.entity.User
import com.taali.enum.UserStatus
import com.taali.repository.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import java.time.LocalDateTime
import java.util.*
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.LoggerFactory

@ApplicationScoped
class AuthService {

    @Inject lateinit var passwordService: PasswordService

    @Inject lateinit var userRepository: UserRepository

    @Inject lateinit var otpService: OtpService

    @Inject lateinit var emailService: EmailService

    @ConfigProperty(name = "app.jwt.refresh-token.expiration-hours", defaultValue = "720")
    var refreshTokenExpirationHours: Long = 720

    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    @Transactional
    fun register(registerRequest: RegisterRequest): RegisterResponse {
        try {
            // Check if user already exists
            if (userRepository.findByEmail(registerRequest.email) != null) {
                return RegisterResponse.error("User with this email already exists")
            }

            if (userRepository.findByPhoneNumber(registerRequest.phoneNumber) != null) {
                return RegisterResponse.error("User with this phone number already exists")
            }

            // Generate OTP
            val otpCode = otpService.generateOtp()
            val userId = UUID.randomUUID().toString()

            // Store temporary registration with OTP
            otpService.storePendingRegistration(userId, registerRequest, otpCode)

            // Send OTP via email - using the email service
            emailService.sendOtpEmail(
                            email = registerRequest.email,
                            userName = "${registerRequest.firstName} ${registerRequest.lastName}",
                            otpCode = otpCode
                    )
                    .exceptionally { throwable ->
                        logger.error(
                                "Failed to send OTP email to: ${registerRequest.email}",
                                throwable
                        )
                        null
                    }

            logger.info("OTP sent for user registration: ${registerRequest.email}")

            return RegisterResponse.otpRequired(
                    message = "Please verify your email with the OTP code sent",
                    userId = userId,
                    email = registerRequest.email,
                    otpCode = otpCode // Remove this in production, only for development
            )
        } catch (e: Exception) {
            logger.error("Registration failed for email: ${registerRequest.email}", e)
            return RegisterResponse.error("Registration failed: ${e.message}")
        }
    }

    @Transactional
    fun verifyOtp(otpRequest: OtpVerificationRequest): RegisterResponse {
        try {
            // Get pending registration
            val pendingRegistration = otpService.getPendingRegistration(otpRequest.userId)
            if (pendingRegistration == null) {
                return RegisterResponse.error("Invalid or expired registration session")
            }

            // Verify OTP
            if (!otpService.verifyOtp(otpRequest.userId, otpRequest.otp)) {
                return RegisterResponse.error("Invalid OTP code")
            }

            // OTP is valid, create the user
            val registerRequest = pendingRegistration.request
            val user =
                    User().apply {
                        firstName = registerRequest.firstName
                        lastName = registerRequest.lastName
                        email = registerRequest.email
                        phoneNumber = registerRequest.phoneNumber
                        passwordHash = passwordService.hashPassword(registerRequest.password)
                        role = registerRequest.role
                        status =
                                UserStatus
                                        .ACTIVE // Changed from PENDING to ACTIVE after verification
                        createdAt = LocalDateTime.now()
                        updatedAt = LocalDateTime.now()
                    }

            // Save user to database
            user.persist()

            // Clean up pending registration
            otpService.removePendingRegistration(otpRequest.userId)

            // Send welcome email
            emailService.sendWelcomeEmail(
                            email = user.email,
                            userName = "${user.firstName} ${user.lastName}"
                    )
                    .exceptionally { throwable ->
                        logger.error("Failed to send welcome email to: ${user.email}", throwable)
                        null
                    }

            logger.info("User registered successfully: ${user.email}")

            return RegisterResponse.success(
                    message = "Registration completed successfully",
                    userId = user.id.toString(),
                    email = user.email,
                    role = user.role,
                    status = user.status
            )
        } catch (e: Exception) {
            logger.error("OTP verification failed for userId: ${otpRequest.userId}", e)
            return RegisterResponse.error("OTP verification failed: ${e.message}")
        }
    }

    @Transactional
    fun resendOtp(resendRequest: ResendOtpRequest): RegisterResponse {
        try {
            // Get pending registration
            val pendingRegistration = otpService.getPendingRegistration(resendRequest.userId)
            if (pendingRegistration == null) {
                return RegisterResponse.error("Invalid registration session")
            }

            // Generate new OTP
            val newOtpCode = otpService.generateOtp()
            otpService.updateOtp(resendRequest.userId, newOtpCode)

            // Send new OTP via email
            emailService.sendOtpEmail(
                            email = pendingRegistration.request.email,
                            userName =
                                    "${pendingRegistration.request.firstName} ${pendingRegistration.request.lastName}",
                            otpCode = newOtpCode
                    )
                    .exceptionally { throwable ->
                        logger.error(
                                "Failed to resend OTP email to: ${pendingRegistration.request.email}",
                                throwable
                        )
                        null
                    }

            logger.info("OTP resent for user: ${pendingRegistration.request.email}")

            return RegisterResponse.otpRequired(
                    message = "New verification code sent",
                    userId = resendRequest.userId,
                    email = pendingRegistration.request.email,
                    otpCode = newOtpCode // Remove this in production
            )
        } catch (e: Exception) {
            logger.error("Resend OTP failed for userId: ${resendRequest.userId}", e)
            return RegisterResponse.error("Failed to resend OTP: ${e.message}")
        }
    }

    @Transactional
    fun createRefreshToken(user: User): RefreshToken {
        // Revoke existing tokens for this user
        revokeUserRefreshTokens(user)

        // Create new refresh token
        val refreshToken =
                RefreshToken().apply {
                    this.user = user
                    this.token = UUID.randomUUID().toString()
                    this.expiresAt = LocalDateTime.now().plusHours(refreshTokenExpirationHours)
                    this.isRevoked = false
                    this.createdAt = LocalDateTime.now()
                }

        refreshToken.persist()

        return refreshToken
    }

    @Transactional
    fun revokeUserRefreshTokens(user: User) {
        RefreshToken.find("user = ?1 and isRevoked = false", user).list().forEach { token ->
            token.isRevoked = true
            token.persist()
        }
    }

    @Transactional
    fun rotateRefreshToken(oldToken: String): RefreshToken? {
        val refreshToken = RefreshToken.findByToken(oldToken)
        return if (refreshToken != null && refreshToken.isValid()) {
            refreshToken.revoke()
            createRefreshToken(refreshToken.user)
        } else {
            null
        }
    }

    fun validateCredentials(email: String, password: String): User? {
        val user = userRepository.findByEmail(email)
        return if (user != null && passwordService.verifyPassword(password, user.passwordHash)) {
            user
        } else {
            null
        }
    }
}
