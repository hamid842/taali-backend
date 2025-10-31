package com.taali.api.dto.auth.response

import java.util.UUID

data class SchoolDto(
    val id: Long?,
    val name: String,
    val code: String
)

data class LoginResponseDto(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val refreshToken: String? = null,
    val userId: UUID? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val role: String? = null,
    val permissions: List<String> = emptyList(),
    val availableSchools: List<SchoolDto> = emptyList(),
    val currentSchool: SchoolDto? = null
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
            role: String,
            permissions: List<String>,
            availableSchools: List<SchoolDto>,
            currentSchool: SchoolDto?
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
                role = role,
                permissions = permissions,
                availableSchools = availableSchools,
                currentSchool = currentSchool
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