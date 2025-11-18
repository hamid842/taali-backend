package com.taali.api.dto.school.response

import com.taali.domain.enum.TimestampType

data class ClassTimestampResponse(
    val id: Long,
    val name: String,
    val startTime: String,
    val endTime: String,
    val type: TimestampType,
    val orderIndex: Int,
    val isActive: Boolean,
    val description: String?
)