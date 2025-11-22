package com.taali.api.dto.parent.response

import com.taali.domain.model.school.Parent

data class ParentResponse(
    val id: Long,
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?,
    val occupation: String?,
    val address: String?,
    val isActive: Boolean
) {
    companion object {
        fun fromEntity(parent: Parent): ParentResponse {
            return ParentResponse(
                id = parent.id ?: throw IllegalArgumentException("Parent must have an ID"),
                userId = parent.user?.id ?: throw IllegalArgumentException("Parent must have a user"),
                firstName = parent.user?.firstName ?: "",
                lastName = parent.user?.lastName ?: "",
                email = parent.user?.email ?: "",
                phoneNumber = parent.user?.phoneNumber,
                occupation = parent.occupation,
                address = parent.address,
                isActive = parent.isActive
            )
        }
    }
}
