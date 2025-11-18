package com.taali.api.dto.school.request

import com.taali.domain.enum.TimestampType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateClassTimestampRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val startTime: String, // HH:mm
    @field:NotBlank val endTime: String,   // HH:mm
    @field:NotNull val type: TimestampType,
    val orderIndex: Int? = null,
    val description: String? = null
)
