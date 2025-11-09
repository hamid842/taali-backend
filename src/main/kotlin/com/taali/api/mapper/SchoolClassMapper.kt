package com.taali.api.mapper

import com.taali.api.dto.school.SchoolSummary
import com.taali.api.dto.school.StudentSummary
import com.taali.api.dto.school.TeacherSummary
import com.taali.api.dto.school.response.ClassScheduleResponse
import com.taali.api.dto.school.response.SchoolClassDetailResponse
import com.taali.api.dto.school.response.SchoolClassResponse
import com.taali.domain.model.school.*

object SchoolClassMapper {

    fun toResponse(schoolClass: SchoolClass): SchoolClassResponse {
        return SchoolClassResponse(
            id = schoolClass.id!!,
            name = schoolClass.name,
            gradeLevel = schoolClass.gradeLevel,
            academicYear = schoolClass.academicYear,
            capacity = schoolClass.capacity,
            isActive = schoolClass.isActive,
            school = schoolClass.school?.let {
                SchoolSummary(it.id!!, it.name, it.code)
            },
            mainTeacher = schoolClass.mainTeacher?.let {
                TeacherSummary(it.id!!, it.user?.firstName ?: "", it.user?.lastName ?: "", it.user?.email)
            },
            studentCount = schoolClass.students.size,
            teacherCount = schoolClass.teachers.size,
            createdAt = schoolClass.createdAt,
            updatedAt = schoolClass.updatedAt
        )
    }

    fun toDetailResponse(schoolClass: SchoolClass, schedules: List<ClassSchedule> = emptyList()): SchoolClassDetailResponse {
        return SchoolClassDetailResponse(
            id = schoolClass.id!!,
            name = schoolClass.name,
            gradeLevel = schoolClass.gradeLevel,
            academicYear = schoolClass.academicYear,
            capacity = schoolClass.capacity,
            isActive = schoolClass.isActive,
            school = schoolClass.school?.let {
                SchoolSummary(it.id!!, it.name, it.code)
            },
            mainTeacher = schoolClass.mainTeacher?.let {
                TeacherSummary(it.id!!, it.user?.firstName ?: "", it.user?.lastName ?: "", it.user?.email)
            },
            students = schoolClass.students.map {
                StudentSummary(it.id!!, it.user?.firstName ?: "", it.user?.lastName ?: "", it.studentId)
            },
            teachers = schoolClass.teachers.map {
                TeacherSummary(it.id!!, it.user?.firstName ?: "", it.user?.lastName ?: "", it.user?.email)
            },
            schedules = schedules.map { toScheduleResponse(it) },
            createdAt = schoolClass.createdAt,
            updatedAt = schoolClass.updatedAt
        )
    }

    fun toScheduleResponse(schedule: ClassSchedule): ClassScheduleResponse {
        return ClassScheduleResponse(
            id = schedule.id!!,
            classId = schedule.schoolClass?.id!!, // This was likely missing
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
}