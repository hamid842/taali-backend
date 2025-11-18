package com.taali.api.dto.teacher

import java.time.LocalDateTime

data class UpcomingClass(
    val id: Long,
    val className: String,
    val subject: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val room: String?,
    val studentCount: Long
)
