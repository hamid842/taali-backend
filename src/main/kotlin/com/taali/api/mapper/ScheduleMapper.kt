package com.taali.api.mapper

import com.taali.api.dto.school.TeacherSummary
import com.taali.api.dto.school.response.ClassScheduleResponse
import com.taali.api.dto.school.response.LessonResponse
import com.taali.domain.model.school.ClassSchedule
import com.taali.domain.model.school.Lesson

object ScheduleMapper {

    fun toResponse(schedule: ClassSchedule): ClassScheduleResponse {
        return ClassScheduleResponse(
            id = schedule.id!!,
            classId = schedule.schoolClass?.id!!,
            dayOfWeek = schedule.dayOfWeek!!,
            startTime = schedule.startTime!!,
            endTime = schedule.endTime!!,
            subjectName = schedule.subjectName,
            teacher = schedule.teacher?.let {
                TeacherSummary(it.id!!, it.user?.firstName ?: "", it.user?.lastName ?: "", it.user?.email)
            },
            roomNumber = schedule.roomNumber
        )
    }

    fun toLessonResponse(lesson: Lesson): LessonResponse {
        return LessonResponse(
            id = lesson.id!!,
            name = lesson.name,
            nameEn = lesson.nameEn,
            gradeLevel = lesson.gradeLevel,
            color = lesson.color,
            createdAt = lesson.createdAt,
            updatedAt = lesson.updatedAt
        )
    }
}