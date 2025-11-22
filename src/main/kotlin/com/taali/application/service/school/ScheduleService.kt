package com.taali.application.service.school

import com.taali.api.dto.school.request.CreateClassScheduleRequest
import com.taali.api.dto.school.request.UpdateClassScheduleRequest
import com.taali.api.dto.school.response.ClassScheduleResponse
import com.taali.api.dto.school.response.LessonResponse
import com.taali.api.mapper.ScheduleMapper
import com.taali.domain.model.school.*
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.jboss.logging.Logger
import java.time.DayOfWeek

@ApplicationScoped
class ScheduleService {

    @Inject
    lateinit var entityManager: EntityManager

    private val logger: Logger = Logger.getLogger(ScheduleService::class.java)

    // Existing schedule methods...
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

    // GET AVAILABLE GRADE LEVELS FOR SPECIFIC SCHOOL
    fun getAvailableGradeLevelsForSchool(schoolId: Long): List<String> {
        logger.info("Retrieving available grade levels for school: $schoolId")

        return try {
            // Use the repository method you created
            val lessons = Lesson.find("school_id", schoolId).list()
            val gradeLevels = lessons.map { it.gradeLevel }.distinct()

            logger.info("Found available grade levels for school $schoolId: $gradeLevels")
            gradeLevels
        } catch (e: Exception) {
            logger.error("Failed to retrieve available grade levels for school: $schoolId", e)
            emptyList()
        }
    }

    // GET LESSONS BY GRADE LEVEL FOR SPECIFIC SCHOOL
    fun getLessonsByGradeLevel(gradeLevel: String, schoolId: Long): List<LessonResponse> {
        logger.info("Retrieving lessons for grade level: $gradeLevel in school: $schoolId")

        return try {
            // Use the repository method you created
            val lesson = Lesson.findByGradeLevelAndSchoolId(gradeLevel, schoolId)
            val lessons = if (lesson != null) listOf(lesson) else emptyList()

            lessons.map { ScheduleMapper.toLessonResponse(it) }
        } catch (e: Exception) {
            logger.error("Failed to retrieve lessons for grade level: $gradeLevel in school: $schoolId", e)
            emptyList()
        }
    }

    // GET LESSONS BY MULTIPLE GRADE LEVELS FOR SPECIFIC SCHOOL
    fun getLessonsByGradeLevels(gradeLevels: List<String>, schoolId: Long): List<LessonResponse> {
        logger.info("Retrieving lessons for grade levels: $gradeLevels in school: $schoolId")

        if (gradeLevels.isEmpty()) {
            logger.warn("No grade levels provided for filtering")
            return emptyList()
        }

        return try {
            // Use the repository method you created
            val lessons = Lesson.findByGradeLevelsAndSchoolId(gradeLevels, schoolId)
            lessons.map { ScheduleMapper.toLessonResponse(it) }
        } catch (e: Exception) {
            logger.error("Failed to retrieve lessons for grade levels: $gradeLevels in school: $schoolId", e)
            emptyList()
        }
    }

    // Fix the findAvailableGradeLevelsBySchool method to return List<String>
    fun getAvailableGradeLevelsForSchoolNative(schoolId: Long): List<String> {
        logger.info("Retrieving available grade levels for school using native query: $schoolId")

        return try {
            // Since your repository method returns List<Lesson>, we need to extract gradeLevels
            val lessons = Lesson.find("school_id", schoolId).list()
            val gradeLevels = lessons.map { it.gradeLevel }.distinct()

            logger.info("Found available grade levels for school $schoolId: $gradeLevels")
            gradeLevels
        } catch (e: Exception) {
            logger.error("Failed to retrieve available grade levels for school: $schoolId", e)
            emptyList()
        }
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

    fun getTeacherSchedule(teacherId: Long): List<ClassScheduleResponse> {
        return ClassSchedule.findByTeacher(teacherId).map { ScheduleMapper.toResponse(it) }
    }

    // NEW LESSON MANAGEMENT METHODS
    fun getAllLessons(): List<LessonResponse> {
        logger.info("Retrieving all lessons")
        return Lesson.listAll().map { ScheduleMapper.toLessonResponse(it) }
    }

    fun getLessonsByGradeLevel(gradeLevel: String): List<LessonResponse> {
        logger.info("Retrieving lessons for grade level: $gradeLevel")
        return Lesson.findByGradeLevel(gradeLevel).map { ScheduleMapper.toLessonResponse(it) }
    }

    fun getLessonById(id: Long): LessonResponse {
        logger.info("Retrieving lesson with ID: $id")
        val lesson = Lesson.findById(id)
            ?: throw EntityNotFoundException("Lesson not found with ID: $id")
        return ScheduleMapper.toLessonResponse(lesson)
    }

    @Transactional
    fun createLesson(lesson: Lesson): LessonResponse {
        logger.info("Creating new lesson: ${lesson.name}")

        // Validate required fields
        if (lesson.name.isBlank()) {
            throw IllegalArgumentException("Lesson name is required")
        }
        if (lesson.gradeLevel.isBlank()) {
            throw IllegalArgumentException("Grade level is required")
        }

        // Check if lesson with same name and grade level already exists
        val existingLesson = Lesson.findByNameAndGradeLevel(lesson.name, lesson.gradeLevel)
        if (existingLesson != null) {
            throw IllegalArgumentException("Lesson with name '${lesson.name}' already exists for grade level '${lesson.gradeLevel}'")
        }

        lesson.persist()
        logger.info("Successfully created lesson with ID: ${lesson.id}")
        return ScheduleMapper.toLessonResponse(lesson)
    }

    @Transactional
    fun updateLesson(id: Long, updatedLesson: Lesson): LessonResponse {
        logger.info("Updating lesson with ID: $id")

        val existingLesson = Lesson.findById(id)
            ?: throw EntityNotFoundException("Lesson not found with ID: $id")

        // Update fields if provided
        if (updatedLesson.name.isNotBlank()) {
            existingLesson.name = updatedLesson.name
        }
        if (updatedLesson.nameEn != null) {
            existingLesson.nameEn = updatedLesson.nameEn
        }
        if (updatedLesson.gradeLevel.isNotBlank()) {
            existingLesson.gradeLevel = updatedLesson.gradeLevel
        }
        if (updatedLesson.color != null) {
            existingLesson.color = updatedLesson.color
        }

        // Check for duplicate name in the same grade level
        if (updatedLesson.name.isNotBlank() && updatedLesson.gradeLevel.isNotBlank()) {
            val duplicateLesson = Lesson.findByNameAndGradeLevel(updatedLesson.name, updatedLesson.gradeLevel)
            if (duplicateLesson != null && duplicateLesson.id != id) {
                throw IllegalArgumentException("Lesson with name '${updatedLesson.name}' already exists for grade level '${updatedLesson.gradeLevel}'")
            }
        }

        existingLesson.persist()
        logger.info("Successfully updated lesson with ID: $id")
        return ScheduleMapper.toLessonResponse(existingLesson)
    }

    @Transactional
    fun deleteLesson(id: Long) {
        logger.info("Deleting lesson with ID: $id")

        val lesson = Lesson.findById(id)
            ?: throw EntityNotFoundException("Lesson not found with ID: $id")

        lesson.delete()
        logger.info("Successfully deleted lesson with ID: $id")
    }
}