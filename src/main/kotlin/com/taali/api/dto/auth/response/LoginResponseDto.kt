package com.taali.api.dto.auth.response

import java.util.UUID

data class LoginResponseDto(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val refreshToken: String? = null,
    val userId: UUID? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val role: String? = null
) {
    companion object {
        fun success(
            message: String,
            token: String,
            refreshToken: String,
            userId: UUID,
            email: String,
            firstName: String,
            lastName: String,
            role: String
        ): LoginResponseDto {
            return LoginResponseDto(
                success = true,
                message = message,
                token = token,
                refreshToken = refreshToken,
                userId = userId,
                email = email,
                firstName = firstName,
                lastName = lastName,
                role = role
            )
        }

        fun failure(message: String): LoginResponseDto {
            return LoginResponseDto(
                success = false,
                message = message
            )
        }
    }
}