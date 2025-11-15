package com.taali.application.service.school

import com.taali.domain.model.school.Teacher
import com.taali.domain.model.school.SchoolClass
import com.taali.domain.model.school.ClassTeacher
import com.taali.api.dto.teacher.*
import com.taali.api.dto.teacher.response.TeacherClassResponse
import com.taali.api.dto.teacher.response.TeacherDetailResponse
import com.taali.api.dto.teacher.response.TeacherListResponse
import com.taali.domain.model.school.Student
import com.taali.domain.repository.school.SchoolClassRepository
import com.taali.domain.repository.teacher.TeacherRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@ApplicationScoped
class TeacherService {

    @Inject
    lateinit var teacherRepository: TeacherRepository

    @Inject
    lateinit var classRepository: SchoolClassRepository

    @Inject
    lateinit var scheduleService: ScheduleService

    fun getTeachersBySchool(schoolId: Long): List<TeacherListResponse> {
        val teachers = Teacher.find("user.school.id", schoolId).list()
        return teachers.map { teacher ->
            TeacherListResponse(
                id = teacher.id!!,
                firstName = teacher.user?.firstName ?: "",
                lastName = teacher.user?.lastName ?: "",
                email = teacher.user?.email ?: "",
                profileImage = teacher.user?.profileImage,
                isActive = teacher.isActive,
                subjects = teacher.specializations.toList(),
                classCount = ClassTeacher.findByTeacher(teacher.id!!).size
            )
        }
    }

    fun getTeacherById(id: Long): TeacherDetailResponse? {
        val teacher = Teacher.findByUser(id)

        val classAssignments = ClassTeacher.findByTeacher(teacher?.id!!)

        return TeacherDetailResponse(
            id = teacher.id!!,
            firstName = teacher.user?.firstName ?: "",
            lastName = teacher.user?.lastName ?: "",
            email = teacher.user?.email ?: "",
            profileImage = teacher.user?.profileImage,
            isActive = teacher.isActive,
            qualification = teacher.qualification,
            experienceYears = teacher.experienceYears,
            specializations = teacher.specializations.toList(),
            classes = classAssignments.map { assignment ->
                val schoolClass = assignment.schoolClass
                val studentCount = if (schoolClass != null) {
                    Student.count("schoolClass", schoolClass)
                } else 0
                TeacherClassResponse(
                    classId = assignment.schoolClass?.id ?: 0,
                    className = assignment.schoolClass?.name ?: "Unknown",
                    subject = assignment.subject,
                    isMainTeacher = assignment.isMainTeacher,
                    studentCount = studentCount
                )
            }
        )
    }

    fun getTeacherClasses(userId: Long): List<Any> {
        val teacher = Teacher.findByUser(userId) ?: return emptyList()

        val classAssignments = ClassTeacher.findByTeacher(teacher.id!!)

        return classAssignments.map { classTeacher ->
            val schoolClass = classTeacher.schoolClass
            val studentCount = if (schoolClass != null) {
                Student.count("schoolClass", schoolClass)
            } else 0
            TeacherClassResponse(
                classId = classTeacher.schoolClass?.id ?: 0,
                className = classTeacher.schoolClass?.name ?: "Unknown Class",
                subject = classTeacher.subject,
                isMainTeacher = classTeacher.isMainTeacher,
                studentCount = studentCount
            )
        }
    }

    // NEW DASHBOARD METHODS USING PROPER SCHEDULE SERVICE

    fun getTeacherDashboardStats(teacherId: Long): TeacherDashboardStats {
        val teacher = Teacher.findById(teacherId) ?: throw IllegalArgumentException("Teacher not found")

        // Get teacher's class assignments using ClassTeacher
        val classAssignments = ClassTeacher.findByTeacher(teacherId)
        val teacherClasses = classAssignments.mapNotNull { it.schoolClass }

        // Calculate total students across all classes
        val totalStudents = teacherClasses.flatMap { it.students }.distinct().size

        // Calculate total classes
        val totalClasses = teacherClasses.size

        // Get today's upcoming classes using schedule service
        val today = LocalDate.now()
        val upcomingClasses = teacherClasses.count { schoolClass ->
            val todaySchedules = scheduleService.getSchedulesByClassAndDay(schoolClass.id!!, today.dayOfWeek)
            todaySchedules.isNotEmpty()
        }

        // Calculate attendance rate
        val attendanceRate = calculateTeacherAttendanceRate(teacherId)

        return TeacherDashboardStats(
            totalStudents = totalStudents,
            totalClasses = totalClasses,
            upcomingClasses = upcomingClasses,
            attendanceRate = attendanceRate
        )
    }

    fun getTeacherClassesWithDetails(teacherId: Long): List<TeacherClassDetail> {
        val classAssignments = ClassTeacher.findByTeacher(teacherId)

        return classAssignments.mapNotNull { classTeacher ->
            val schoolClass = classTeacher.schoolClass ?: return@mapNotNull null

            // Get all schedules for this class
            val schedules = scheduleService.getSchedulesByClass(schoolClass.id!!)
            val scheduleString = schedules.joinToString(", ") { schedule ->
                "${schedule.dayOfWeek} ${schedule.startTime} - ${schedule.endTime}"
            }

            TeacherClassDetail(
                id = schoolClass.id!!,
                className = schoolClass.name,
                subject = classTeacher.subject ?: "General",
                gradeLevel = schoolClass.gradeLevel ?: "N/A",
                studentCount = schoolClass.students.size,
                schedule = scheduleString.ifEmpty { "Schedule not set" },
                room = getClassRoom(schoolClass, schedules)
            )
        }
    }

    fun getUpcomingClasses(teacherId: Long): List<UpcomingClass> {
        val classAssignments = ClassTeacher.findByTeacher(teacherId)
        val today = LocalDate.now()

        return classAssignments.flatMap { classTeacher ->
            val schoolClass = classTeacher.schoolClass ?: return@flatMap emptyList<UpcomingClass>()

            // Get today's schedules for this class
            val todaySchedules = scheduleService.getSchedulesByClassAndDay(schoolClass.id!!, today.dayOfWeek)

            todaySchedules.map { schedule ->
                UpcomingClass(
                    id = schoolClass.id!!,
                    className = schoolClass.name,
                    subject = classTeacher.subject ?: schedule.subjectName,
                    startTime = LocalDateTime.of(today, schedule.startTime),
                    endTime = LocalDateTime.of(today, schedule.endTime),
                    room = schedule.roomNumber ?: getClassRoom(
                        schoolClass,
                        listOf(schedule)
                    ), // Fixed: Use helper method
                    studentCount = schoolClass.students.size
                )
            }
        }.sortedBy { it.startTime }
    }

    fun getTodaySchedule(teacherId: Long): List<UpcomingClass> {
        return getUpcomingClasses(teacherId)
    }

    fun getRecentActivity(teacherId: Long): List<TeacherActivity> {
        // Get teacher's classes to use for activity context
        val classAssignments = ClassTeacher.findByTeacher(teacherId)

        // Mock activities - in real implementation, you'd query actual activity records
        return classAssignments.take(3).mapIndexed { index, classTeacher ->
            val schoolClass = classTeacher.schoolClass
            TeacherActivity(
                id = (index + 1).toLong(),
                type = when (index) {
                    0 -> "attendance"
                    1 -> "assignment"
                    else -> "grading"
                },
                title = when (index) {
                    0 -> "Attendance Taken"
                    1 -> "Assignment Created"
                    else -> "Grades Updated"
                },
                description = when (index) {
                    0 -> "Marked attendance for ${schoolClass?.name ?: "class"}"
                    1 -> "Created new homework assignment"
                    else -> "Updated grades for ${classTeacher.subject ?: "subject"} exam"
                },
                timestamp = LocalDateTime.now().minusHours((index + 1).toLong() * 2),
                classId = schoolClass?.id
            )
        }
    }

    private fun calculateTeacherAttendanceRate(teacherId: Long): Double {
        // For now, return a mock value
        // In real implementation, you'd calculate based on actual attendance records
        return 85.5
    }

    // HELPER METHOD TO GET ROOM INFORMATION
    private fun getClassRoom(
        schoolClass: SchoolClass,
        schedules: List<com.taali.api.dto.school.response.ClassScheduleResponse>
    ): String {
        // Try to get room from schedules first
        val scheduleRoom = schedules.firstOrNull()?.roomNumber
        if (!scheduleRoom.isNullOrBlank()) {
            return scheduleRoom
        }

        // Fallback: check if SchoolClass has any room-related property
        return try {
            // Try common room property names
            when {
                // If SchoolClass has a roomNumber property
                schoolClass::class.members.any { it.name == "roomNumber" } -> {
                    schoolClass.javaClass.getMethod("getRoomNumber").invoke(schoolClass) as? String
                        ?: "Room not assigned"
                }
                // If SchoolClass has a room property
                schoolClass::class.members.any { it.name == "room" } -> {
                    schoolClass.javaClass.getMethod("getRoom").invoke(schoolClass) as? String ?: "Room not assigned"
                }
                // If SchoolClass has a location property
                schoolClass::class.members.any { it.name == "location" } -> {
                    schoolClass.javaClass.getMethod("getLocation").invoke(schoolClass) as? String ?: "Room not assigned"
                }

                else -> "Room not assigned"
            }
        } catch (e: Exception) {
            "Room not assigned"
        }
    }

    @Transactional
    fun createTeacher(teacher: Teacher): Teacher {
        teacher.persist()
        return teacher
    }

    @Transactional
    fun updateTeacher(id: Long, teacherData: Map<String, Any>): Teacher? {
        val teacher = Teacher.findById(id) ?: return null

        teacherData["qualification"]?.let { teacher.qualification = it.toString() }
        teacherData["experienceYears"]?.let { teacher.experienceYears = it.toString().toIntOrNull() }
        teacherData["hireDate"]?.let {
            // Handle date conversion if needed
        }
        teacherData["isActive"]?.let { teacher.isActive = it.toString().toBoolean() }

        // Handle specializations
        teacherData["specializations"]?.let {
            if (it is List<*>) {
                teacher.specializations.clear()
                teacher.specializations.addAll(it.filterIsInstance<String>())
            }
        }

        return teacher
    }

    @Transactional
    fun deleteTeacher(id: Long): Boolean {
        val teacher = Teacher.findById(id) ?: return false

        // First delete class assignments to avoid foreign key constraints
        val classAssignments = ClassTeacher.findByTeacher(id)
        classAssignments.forEach { it.delete() }

        teacher.delete()
        return true
    }

    fun getActiveTeachersBySchool(schoolId: Long): List<Teacher> {
        return Teacher.find("user.school.id = ?1 and isActive = ?2", schoolId, true).list()
    }

    // Search methods
    fun searchTeachers(schoolId: Long, searchTerm: String): List<Teacher> {
        return Teacher.find(
            "user.school.id = ?1 and (user.firstName like ?2 or user.lastName like ?2 or user.email like ?2)",
            schoolId,
            "%$searchTerm%"
        ).list()
    }

    fun getTeachersBySubject(schoolId: Long, subject: String): List<Teacher> {
        return Teacher.find(
            "user.school.id = ?1 and specializations like ?2",
            schoolId,
            "%$subject%"
        ).list()
    }

    @Transactional
    fun updateTeacherStatus(id: Long, isActive: Boolean): Teacher? {
        val teacher = Teacher.findById(id) ?: return null
        teacher.isActive = isActive
        return teacher
    }

    @Transactional
    fun assignClassesToTeacher(teacherId: Long, classIds: List<Long>): Boolean {
        val teacher = teacherRepository.findById(teacherId) ?: return false

        // Clear existing class assignments for this teacher
        val existingAssignments = ClassTeacher.findByTeacher(teacherId)
        existingAssignments.forEach { it.delete() }

        // Create new ClassTeacher relationships
        classIds.forEach { classId ->
            val schoolClass = classRepository.findById(classId)
            schoolClass?.let {
                val classTeacher = ClassTeacher().apply {
                    this.teacher = teacher
                    this.schoolClass = it
                    this.subject = teacher.specializations.firstOrNull() ?: "General"
                    this.isMainTeacher = false // Default to false, can be updated later
                }
                classTeacher.persist()
            }
        }

        return true
    }
}