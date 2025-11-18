package com.taali.api.dto.school.response

import com.taali.domain.enum.TimestampType

data class AvailableTimestampTypesResponse(
    val availableTypes: List<TimestampTypeInfo>
)

data class TimestampTypeInfo(
    val type: TimestampType,
    val label: String,
    val enabled: Boolean
)