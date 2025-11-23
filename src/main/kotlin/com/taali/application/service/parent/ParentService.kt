package com.taali.application.service.parent

import com.taali.api.dto.auth.request.RegisterRequestDto
import com.taali.api.dto.parent.request.CreateParentRequest
import com.taali.api.dto.parent.response.*
import com.taali.api.dto.school.StudentDTO
import com.taali.application.service.user.UserService
import com.taali.domain.enum.UserRole
import com.taali.domain.model.school.Parent
import com.taali.domain.model.school.Student
import com.taali.domain.model.user.User
import io.quarkus.panache.common.Parameters
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import java.time.LocalDate

@ApplicationScoped
class ParentService {

    @Inject
    lateinit var userService: UserService

    private val logger = LoggerFactory.getLogger(ParentService::class.java)

    @Transactional
    fun createParent(createParentRequest: CreateParentRequest, locale: String = "en"): ParentResponse {
        try {
            // First create the user using your existing registration
            val registerRequest = RegisterRequestDto(
                firstName = createParentRequest.firstName,
                lastName = createParentRequest.lastName,
                email = createParentRequest.email,
                phoneNumber = createParentRequest.phoneNumber,
                password = generateTemporaryPassword(),
                role = UserRole.PARENT,
                schoolId = null // Parents might not be associated with a school directly
            )

            val registerResponse = userService.registerUser(registerRequest, locale)

            if (!registerResponse.success) {
                throw IllegalArgumentException(registerResponse.message)
            }

            // Find the created user
            val user = User.find("email", createParentRequest.email.lowercase()).firstResult()
                ?: throw IllegalArgumentException("User not found after registration")

            // Then create the parent record
            val parent = Parent().apply {
                this.user = user
                this.occupation = createParentRequest.occupation
                this.address = createParentRequest.address
                this.isActive = true
            }

            parent.persist()

            logger.info("Created parent with ID: ${parent.id} for user: ${user.email}")

            return ParentResponse.fromEntity(parent)

        } catch (e: Exception) {
            logger.error("Failed to create parent for email: ${createParentRequest.email}", e)
            throw e
        }
    }

    @Transactional
    fun associateParentsWithStudent(studentId: Long, parentIds: List<Long>): StudentDTO {
        val student =
            Student.findByUser(studentId) ?: throw IllegalArgumentException("Student not found with id: $studentId")

        val parents = Parent.find(
            "user.id in :ids", Parameters.with("ids", parentIds)
        ).list()

        if (parents.size != parentIds.size) {
            throw IllegalArgumentException("Some parent IDs were not found")
        }

        // Add student to each parent's students collection
        parents.forEach { parent ->
            if (!parent.students.any { it.id == studentId }) {
                parent.students.add(student)
            }
        }
        // Force initialization of lazy properties within transaction
        student.user?.firstName // Access user properties to ensure they're loaded
        student.user?.lastName
        student.user?.email
        // Add any other lazy properties that will be used in the DTO

        return StudentDTO.fromEntity(student)
    }

    fun findParentsByStudent(studentId: Long): List<ParentResponse> {
        val parents = Parent.find(
            "SELECT p FROM Parent p JOIN p.students s WHERE s.id = ?1", studentId
        ).list()
        return parents.map { ParentResponse.fromEntity(it) }
    }

    fun getDashboardStats(parentEmail: String): DashboardStatsResponse {
        val parent = getParentByEmail(parentEmail)
        val children = parent.students

        val totalChildren = children.size
        val unreadNotifications = 0 // TODO: Implement notification count
        val pendingPayments = 0 // TODO: Implement payment count

        // Calculate overall attendance rate
        val overallAttendanceRate = if (children.isNotEmpty()) {
            children.map { calculateAttendanceRate(it) }.average().toInt()
        } else {
            0
        }

        val upcomingEvents = 0 // TODO: Implement event count

        return DashboardStatsResponse(
            totalChildren = totalChildren,
            unreadNotifications = unreadNotifications,
            pendingPayments = pendingPayments,
            overallAttendanceRate = overallAttendanceRate,
            upcomingEvents = upcomingEvents
        )
    }

    fun getMyChildren(parentEmail: String): List<ChildResponse> {
        val parent = getParentByEmail(parentEmail)
        return parent.students.map { student ->
            ChildResponse(
                id = student.id!!,
                name = student.getFullName(),
                grade = student.gradeLevel ?: "N/A",
                className = student.schoolClass?.name ?: "N/A",
                schoolName = student.user?.school?.name ?: "N/A",
                attendanceRate = calculateAttendanceRate(student),
                averageGrade = calculateAverageGrade(student),
                teacherName = student.schoolClass?.mainTeacher?.user?.let {
                    "${it.firstName} ${it.lastName}"
                } ?: "N/A",
                profileImage = student.user?.profileImage)
        }
    }

    fun getChildDetail(parentEmail: String, childId: Long): ChildDetailResponse {
        val parent = getParentByEmail(parentEmail)
        val student = parent.students.find { it.id == childId }
            ?: throw IllegalArgumentException("Child not found or not associated with parent")

        return ChildDetailResponse(
            id = student.id!!,
            name = student.getFullName(),
            grade = student.gradeLevel ?: "N/A",
            className = student.schoolClass?.name ?: "N/A",
            schoolName = student.user?.school?.name ?: "N/A",
            attendanceRate = calculateAttendanceRate(student),
            averageGrade = calculateAverageGrade(student),
            teacherName = student.schoolClass?.mainTeacher?.user?.let {
                "${it.firstName} ${it.lastName}"
            } ?: "N/A",
            profileImage = student.user?.profileImage,
            birthDate = student.birthDate?.toString(),
            emergencyContact = student.emergencyContact,
            medicalNotes = student.medicalNotes,
            enrollmentDate = student.createdAt.toString())
    }

    fun getChildrenAttendance(parentEmail: String): ChildrenAttendanceResponse {
        val parent = getParentByEmail(parentEmail)
        val childrenAttendance = parent.students.map { student ->
            ChildAttendanceResponse(
                childId = student.id!!,
                childName = student.getFullName(),
                attendanceRate = calculateAttendanceRate(student),
                presentDays = getPresentDaysCount(student),
                absentDays = getAbsentDaysCount(student),
                lateDays = getLateDaysCount(student),
                monthlyAttendance = getMonthlyAttendance(student)
            )
        }

        return ChildrenAttendanceResponse(
            children = childrenAttendance, overallAttendanceRate = if (childrenAttendance.isNotEmpty()) {
                childrenAttendance.map { it.attendanceRate }.average().toInt()
            } else {
                0
            }
        )
    }

    fun getChildrenGrades(parentEmail: String): ChildrenGradesResponse {
        val parent = getParentByEmail(parentEmail)
        val childrenGrades = parent.students.map { student ->
            ChildGradesResponse(
                childId = student.id!!,
                childName = student.getFullName(),
                averageGrade = calculateAverageGrade(student),
                subjectGrades = getSubjectGrades(student),
                recentAssignments = getRecentAssignments(student)
            )
        }

        return ChildrenGradesResponse(children = childrenGrades)
    }

    fun getChildAttendance(parentEmail: String, childId: Long): ChildAttendanceResponse {
        val parent = getParentByEmail(parentEmail)
        val student = parent.students.find { it.id == childId }
            ?: throw IllegalArgumentException("Child not found or not associated with parent")

        return ChildAttendanceResponse(
            childId = student.id!!,
            childName = student.getFullName(),
            attendanceRate = calculateAttendanceRate(student),
            presentDays = getPresentDaysCount(student),
            absentDays = getAbsentDaysCount(student),
            lateDays = getLateDaysCount(student),
            monthlyAttendance = getMonthlyAttendance(student)
        )
    }

    fun getChildGrades(parentEmail: String, childId: Long): ChildGradesResponse {
        val parent = getParentByEmail(parentEmail)
        val student = parent.students.find { it.id == childId }
            ?: throw IllegalArgumentException("Child not found or not associated with parent")

        return ChildGradesResponse(
            childId = student.id!!,
            childName = student.getFullName(),
            averageGrade = calculateAverageGrade(student),
            subjectGrades = getSubjectGrades(student),
            recentAssignments = getRecentAssignments(student)
        )
    }

    private fun getParentByEmail(email: String): Parent {
        return Parent.find("user.email", email.lowercase()).firstResult()
            ?: throw IllegalArgumentException("Parent not found for email: $email")
    }

    private fun calculateAttendanceRate(student: Student): Int {
        // TODO: Implement actual attendance calculation
        // For now, return a mock value
        return (70..95).random()
    }

    private fun calculateAverageGrade(student: Student): String {
        // TODO: Implement actual grade calculation
        // For now, return a mock value
        val grades = listOf("A", "B+", "B", "C+", "C")
        return grades.random()
    }

    private fun getPresentDaysCount(student: Student): Int {
        // TODO: Implement actual present days count
        return (15..20).random()
    }

    private fun getAbsentDaysCount(student: Student): Int {
        // TODO: Implement actual absent days count
        return (0..3).random()
    }

    private fun getLateDaysCount(student: Student): Int {
        // TODO: Implement actual late days count
        return (0..2).random()
    }

    private fun getMonthlyAttendance(student: Student): Map<String, Int> {
        // TODO: Implement actual monthly attendance data
        return mapOf(
            "Jan" to 85,
            "Feb" to 90,
            "Mar" to 88,
            "Apr" to 92,
            "May" to 87,
            "Jun" to 95,
            "Jul" to 0,
            "Aug" to 0,
            "Sep" to 89,
            "Oct" to 91,
            "Nov" to 86,
            "Dec" to 0
        )
    }

    private fun getSubjectGrades(student: Student): List<SubjectGradeResponse> {
        // TODO: Implement actual subject grades
        val subjects = listOf("Mathematics", "Science", "English", "History", "Art")
        return subjects.map { subject ->
            SubjectGradeResponse(
                subjectName = subject,
                grade = listOf("A", "B+", "B", "C+", "C").random(),
                percentage = (75..95).random()
            )
        }
    }

    private fun getRecentAssignments(student: Student): List<AssignmentResponse> {
        // TODO: Implement actual recent assignments
        return listOf(
            AssignmentResponse(
                assignmentName = "Math Homework",
                subject = "Mathematics",
                grade = "A",
                dueDate = LocalDate.now().minusDays(2).toString()
            ), AssignmentResponse(
                assignmentName = "Science Project",
                subject = "Science",
                grade = "B+",
                dueDate = LocalDate.now().minusDays(5).toString()
            )
        )
    }

    private fun generateTemporaryPassword(): String {
        return "Default@123"
    }
}