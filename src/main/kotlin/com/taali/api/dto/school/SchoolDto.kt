package com.taali.api.dto.school

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class SchoolDto(
    val id: Long,
    val name: String,
    val code: String,
    val image: String?,
    val address: String?,
    val email: String?,
    val phone: String?,
    val ownerId: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class CreateSchoolRequest(
    @field:NotBlank(message = "School name is required")
    val name: String,

    @field:NotBlank(message = "Code is required")
    val code: String,
    val image: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
)

data class UpdateSchoolRequest(
    val name: String? = null,
    val code: String? = null,
    val image: String? = null,
    val address: String? = null,
    val email: String? = null,
    val phone: String? = null
)