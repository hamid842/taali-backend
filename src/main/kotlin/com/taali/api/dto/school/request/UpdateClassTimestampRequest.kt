package com.taali.api.dto.school.request

import com.taali.domain.enum.TimestampType

data class UpdateClassTimestampRequest(
    val name: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val type: TimestampType? = null,
    val orderIndex: Int? = null,
    val isActive: Boolean? = null,
    val description: String? = null
)