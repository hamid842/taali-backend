package com.taali.api.dto.school.request

import java.time.DayOfWeek
import java.time.LocalTime

data class CreateClassScheduleRequest(
    val classId: Long,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val subjectName: String,
    val teacherId: Long?,
    val roomNumber: String?
)
