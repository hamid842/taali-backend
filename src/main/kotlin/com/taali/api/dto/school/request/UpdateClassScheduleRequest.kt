package com.taali.api.dto.school.request

import java.time.DayOfWeek
import java.time.LocalTime

data class UpdateClassScheduleRequest(
    val dayOfWeek: DayOfWeek?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val subjectName: String?,
    val teacherId: Long?,
    val roomNumber: String?
)
