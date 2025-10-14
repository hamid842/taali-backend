package com.taali.service

import com.taali.entity.RefreshToken
import com.taali.entity.User
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.LocalDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class AuthService {

    @Inject
    lateinit var passwordService: PasswordService

    @ConfigProperty(name = "app.jwt.refresh-token.expiration-hours", defaultValue = "720")
    var refreshTokenExpirationHours: Long = 720

    @Transactional
    fun createRefreshToken(user: User): RefreshToken {
        // Revoke existing tokens for this user
        revokeUserRefreshTokens(user)

        // Create new refresh token
        val refreshToken = RefreshToken()
        refreshToken.user = user
        refreshToken.token = UUID.randomUUID().toString()
        refreshToken.expiresAt = LocalDateTime.now().plusHours(refreshTokenExpirationHours)
        refreshToken.isRevoked = false
        refreshToken.persist()

        return refreshToken
    }

    @Transactional
    fun revokeUserRefreshTokens(user: User) {
        RefreshToken.update("isRevoked = true where user = ?1 and isRevoked = false", user)
    }

    @Transactional
    fun rotateRefreshToken(oldToken: String): RefreshToken? {
        val refreshToken = RefreshToken.findByToken(oldToken)
        return if (refreshToken != null) {
            refreshToken.revoke()
            createRefreshToken(refreshToken.user)
        } else {
            null
        }
    }
}