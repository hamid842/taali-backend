package com.taali.application.service.school

import com.taali.api.dto.school.request.CreateClassScheduleRequest
import com.taali.api.dto.school.request.UpdateClassScheduleRequest
import com.taali.api.dto.school.response.ClassScheduleResponse
import com.taali.api.dto.school.response.LessonResponse
import com.taali.api.mapper.ScheduleMapper
import com.taali.domain.model.school.*
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.DayOfWeek

@ApplicationScoped
class ScheduleService {

    @Transactional
    fun createSchedule(request: CreateClassScheduleRequest): ClassScheduleResponse {
        // Validate class exists
        val schoolClass = SchoolClass.find("id", request.classId).firstResult()
            ?: throw IllegalArgumentException("Class not found with id: ${request.classId}")

        // Validate time slot is available
        if (ClassSchedule.existsByClassAndTime(request.classId, request.dayOfWeek, request.startTime)) {
            throw IllegalArgumentException("Time slot already occupied for this class")
        }

        // Validate end time is after start time
        if (request.endTime.isBefore(request.startTime) || request.endTime == request.startTime) {
            throw IllegalArgumentException("End time must be after start time")
        }

        // Validate teacher exists if provided
        val teacher = request.teacherId?.let { teacherId ->
            Teacher.find("id", teacherId).firstResult()
                ?: throw IllegalArgumentException("Teacher not found with id: $teacherId")
        }

        val schedule = ClassSchedule().apply {
            this.schoolClass = schoolClass
            this.dayOfWeek = request.dayOfWeek
            this.startTime = request.startTime
            this.endTime = request.endTime
            this.subjectName = request.subjectName
            this.teacher = teacher
            this.roomNumber = request.roomNumber
        }

        schedule.persist()
        return ScheduleMapper.toResponse(schedule)
    }

    fun getSchedulesByClass(classId: Long): List<ClassScheduleResponse> {
        return ClassSchedule.findByClass(classId).map { ScheduleMapper.toResponse(it) }
    }

    fun getSchedulesByClassAndDay(classId: Long, dayOfWeek: DayOfWeek): List<ClassScheduleResponse> {
        return ClassSchedule.findByClassAndDay(classId, dayOfWeek).map { ScheduleMapper.toResponse(it) }
    }

    @Transactional
    fun updateSchedule(scheduleId: Long, request: UpdateClassScheduleRequest): ClassScheduleResponse {
        val schedule = ClassSchedule.find("id", scheduleId).firstResult()
            ?: throw IllegalArgumentException("Schedule not found with id: $scheduleId")

        // If changing time slot, validate it's available
        if (request.dayOfWeek != null && request.startTime != null) {
            val dayOfWeek = request.dayOfWeek
            val startTime = request.startTime
            if (ClassSchedule.existsByClassAndTime(schedule.schoolClass?.id!!, dayOfWeek, startTime, scheduleId)) {
                throw IllegalArgumentException("Time slot already occupied for this class")
            }
        }

        // Validate end time is after start time if both are being updated
        if (request.startTime != null && request.endTime != null) {
            if (request.endTime.isBefore(request.startTime) || request.endTime == request.startTime) {
                throw IllegalArgumentException("End time must be after start time")
            }
        }

        request.dayOfWeek?.let { schedule.dayOfWeek = it }
        request.startTime?.let { schedule.startTime = it }
        request.endTime?.let { schedule.endTime = it }
        request.subjectName?.let { schedule.subjectName = it }
        request.roomNumber?.let { schedule.roomNumber = it }

        // Update teacher if provided
        request.teacherId?.let { teacherId ->
            val teacher = Teacher.find("id", teacherId).firstResult()
                ?: throw IllegalArgumentException("Teacher not found with id: $teacherId")
            schedule.teacher = teacher
        }

        schedule.persist()
        return ScheduleMapper.toResponse(schedule)
    }

    @Transactional
    fun deleteSchedule(scheduleId: Long) {
        val schedule = ClassSchedule.find("id", scheduleId).firstResult()
            ?: throw IllegalArgumentException("Schedule not found with id: $scheduleId")
        schedule.delete()
    }

    fun getLessonsByGradeLevel(gradeLevel: String): List<LessonResponse> {
        return Lesson.findByGradeLevel(gradeLevel).map { ScheduleMapper.toLessonResponse(it) }
    }

    fun getAllLessons(): List<LessonResponse> {
        return Lesson.listAll().map { ScheduleMapper.toLessonResponse(it) }
    }

    fun getTeacherSchedule(teacherId: Long): List<ClassScheduleResponse> {
        return ClassSchedule.findByTeacher(teacherId).map { ScheduleMapper.toResponse(it) }
    }
}