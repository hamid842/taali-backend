package com.taali.api.dto.school.response

import com.taali.api.dto.school.TeacherSummary
import java.time.DayOfWeek
import java.time.LocalTime

data class ClassScheduleResponse(
    val id: Long,
    val classId:Long,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val subjectName: String,
    val teacher: TeacherSummary?,
    val roomNumber: String?
)
