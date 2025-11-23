package com.taali.api.dto.user

import com.taali.api.dto.school.SchoolInfoDto
import com.taali.domain.enum.UserRole
import com.taali.domain.enum.UserStatus
import com.taali.domain.model.user.User
import java.time.LocalDateTime
import java.util.UUID

data class UserDto(
    val id: Long?,
    val userId: UUID?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val role: UserRole?,
    val status: UserStatus?,
    val profileImage: String?,
    val school: SchoolInfoDto?,
    val lastLogin: LocalDateTime?,
    val isActive: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun fromEntity(user: User?): UserDto {
            // Safely handle lazy-loaded school property
            val schoolDto = try {
                SchoolInfoDto.fromEntity(user?.school)
            } catch (e: Exception) {
                null // Handle case where school is lazy-loaded and session is closed
            }

            // Safely handle other potentially lazy-loaded properties
            val createdAt = try {
                user?.createdAt
            } catch (e: Exception) {
                null
            }

            val updatedAt = try {
                user?.updatedAt
            } catch (e: Exception) {
                null
            }

            val lastLogin = try {
                user?.lastLogin
            } catch (e: Exception) {
                null
            }

            return UserDto(
                id = user?.id,
                userId = user?.userId,
                firstName = user?.firstName,
                lastName = user?.lastName,
                email = user?.email,
                phoneNumber = user?.phoneNumber,
                role = user?.role,
                status = user?.status,
                profileImage = user?.profileImage,
                school = schoolDto,
                lastLogin = lastLogin,
                isActive = user?.active == true,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}