package com.taali.application.service.school

import com.taali.api.dto.school.request.ParentAssociationRequest
import com.taali.api.dto.school.request.StudentDetailsRequest
import com.taali.api.dto.school.response.ClassResponse
import com.taali.api.dto.school.response.StudentResponse
import com.taali.api.dto.shared.PagedResponseDto
import com.taali.api.dto.shared.PaginationInfoDto
import com.taali.domain.enum.Gender
import com.taali.domain.enum.UserRole
import com.taali.domain.model.school.Parent
import com.taali.domain.model.school.SchoolClass
import com.taali.domain.model.school.Student
import com.taali.domain.model.user.User
import io.quarkus.panache.common.Page
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.LocalDate
import java.time.Period

@ApplicationScoped
class StudentService {

    fun getStudentsBySchool(
        schoolId: Long,
        page: Int,
        size: Int,
        search: String? = null,
        gradeLevel: String? = null,
        classId: Long? = null
    ): PagedResponseDto<StudentResponse> {

        var query = "user.school.id = ?1"
        var params = mutableListOf<Any>(schoolId)
        var paramIndex = 2

        // Build dynamic query based on filters
        if (!search.isNullOrBlank()) {
            query += " AND (LOWER(user.firstName) LIKE LOWER(?$paramIndex) OR LOWER(user.lastName) LIKE LOWER(?$paramIndex) OR studentId LIKE ?$paramIndex OR user.email LIKE ?$paramIndex)"
            params.add("%$search%")
            paramIndex++
        }

        if (!gradeLevel.isNullOrBlank()) {
            query += " AND gradeLevel = ?$paramIndex"
            params.add(gradeLevel)
            paramIndex++
        }

        if (classId != null) {
            query += " AND schoolClass.id = ?$paramIndex"
            params.add(classId)
            paramIndex++
        }

        // Get paginated results with proper ordering
        val studentPage = Student.find(query, *params.toTypedArray())
            .page(Page.of(page, size))

        val students = studentPage.list()

        // Convert to DTOs with proper null handling and data enrichment
        val studentResponses = students.map { student ->
            // Get profile image from user if available
            val profileImageUrl = student.user?.profileImage

            // Calculate age from birthdate if available
            val age = student.birthDate?.let { calculateAge(it) }

            // Get class name safely
            val className = student.schoolClass?.name ?: "Not Assigned"

            // Format birthdate for display
            val formattedBirthDate = student.birthDate?.toString()

            StudentResponse(
                id = student.id,
                studentId = student.studentId ?: "N/A", // Provide default for null studentId
                idNumber = student.idNumber,
                birthDate = formattedBirthDate,
                gender = student.gender,
                gradeLevel = student.gradeLevel ?: "Not Set", // Provide default
                emergencyContact = student.emergencyContact ?: "Not Provided",
                emergencyPhone = student.emergencyPhone ?: "Not Provided",
                medicalNotes = student.medicalNotes ?: "No medical notes",
                classId = student.schoolClass?.id,
                className = className,
                userId = student.user?.id,
                userFirstName = student.user?.firstName ?: "Unknown",
                userLastName = student.user?.lastName ?: "Unknown",
                userEmail = student.user?.email ?: "No email",
                profileImageUrl = profileImageUrl,
                isActive = student.user?.active ?: false,
                age = age
            )
        }

        return PagedResponseDto(
            items = studentResponses,
            pagination = PaginationInfoDto(
                page = page,
                size = size,
                totalElements = studentPage.count(),
                totalPages = studentPage.pageCount().toLong(),
                hasNext = studentPage.hasNextPage(),
                hasPrevious = page > 0
            )
        )
    }

    // Helper method to calculate age from birthdate
    private fun calculateAge(birthDate: LocalDate): Int {
        val today = LocalDate.now()
        return Period.between(birthDate, today).years
    }

    // Get unique grade levels for filter dropdown
    fun getGradeLevelsBySchool(schoolId: Long): List<Student> {
        return Student.find(
            "SELECT DISTINCT s.gradeLevel FROM Student s WHERE s.user.school.id = ?1 AND s.gradeLevel IS NOT NULL ORDER BY s.gradeLevel",
            schoolId
        ).list()
    }

    // Get classes for filter dropdown
    fun getClassesBySchool(schoolId: Long): List<ClassResponse> {
        return SchoolClass.find("school.id = ?1 ORDER BY name", schoolId)
            .list()
            .map { schoolClass ->
                ClassResponse(
                    id = schoolClass.id!!,
                    name = schoolClass.name,
                    gradeLevel = schoolClass.gradeLevel
                )
            }
    }

    fun getStudentById(id: Long): Student? = Student.findById(id)

    fun getStudentsByClass(classId: Long): List<Student> = Student.find("schoolClass.id", classId).list()

    fun getStudentByUser(userId: String): Student? = Student.find("user.id", userId).firstResult()

    // ------------------------
    // UPDATE STUDENT DETAILS
    // ------------------------
    @Transactional
    fun updateStudentDetailsByUser(userId: Long, details: StudentDetailsRequest): StudentResponse? {
        val student = Student.find("user.id", userId).firstResult() ?: return null

        details.studentId?.let { student.studentId = it }
        details.idNumber?.let { student.idNumber = it }
        details.birthDate?.let { student.birthDate = it }

        details.gender?.let { genderStr ->
            try {
                student.gender = Gender.valueOf(genderStr.uppercase())
            } catch (e: Exception) {
                println("Invalid gender value: $genderStr")
            }
        }

        details.gradeLevel?.let { student.gradeLevel = it }
        details.emergencyContact?.let { student.emergencyContact = it }
        details.emergencyPhone?.let { student.emergencyPhone = it }
        details.medicalNotes?.let { student.medicalNotes = it }

        details.classId?.let { classId ->
            val schoolClass = SchoolClass.findById(classId)
            if (schoolClass != null) {
                student.schoolClass = schoolClass
            }
        }

        student.persist()

        val age = student.birthDate?.let { calculateAge(it) }
        return StudentResponse(
            id = student.id,
            studentId = student.studentId,
            idNumber = student.idNumber,
            birthDate = student.birthDate.toString(),
            gender = student.gender,
            gradeLevel = student.gradeLevel,
            emergencyContact = student.emergencyContact,
            emergencyPhone = student.emergencyPhone,
            medicalNotes = student.medicalNotes,
            classId = student.schoolClass?.id,
            className = student.schoolClass?.name,
            userId = student.user?.id,
            userFirstName = student.user?.firstName,
            userLastName = student.user?.lastName,
            userEmail = student.user?.email,
            isActive = student.isActive,
            profileImageUrl = student.user?.profileImage,
            age = age
        )
    }

    // ------------------------
    // ASSOCIATE PARENTS
    // ------------------------
    @Transactional
    fun associateParentsByUser(userId: String, parentData: ParentAssociationRequest): Map<String, Any> {
        val student = Student.find("user.id", userId).firstResult()
            ?: throw IllegalArgumentException("Student not found for user: $userId")

        val associatedParents = mutableListOf<Long>()
        val createdParents = mutableListOf<Long>()
        val errors = mutableListOf<String>()

        // Use parents field if provided, otherwise fall back to newParents
        val parentsToProcess = parentData.parents.ifEmpty {
            parentData.newParents
        }

        parentData.parentEmails.forEach { email ->
            try {
                val parentUser = User.find("email", email).firstResult()
                if (parentUser == null) {
                    errors.add("User not found with email: $email")
                    return@forEach
                }

                val existingParent = Parent.find("user", parentUser).firstResult()
                val parentEntity = existingParent ?: Parent().apply {
                    user = parentUser
                    isActive = true
                }.also {
                    it.persist()
                    createdParents.add(it.id!!)
                }

                if (student.parents.add(parentEntity)) {
                    associatedParents.add(parentEntity.id!!)
                }

            } catch (e: Exception) {
                errors.add("Error associating parent $email: ${e.message}")
            }
        }

        // Process new parents (from either parents or newParents field)
        parentsToProcess.forEach { newParent ->
            try {
                val existingUser = User.find("email", newParent.email).firstResult()
                val parentUser = existingUser ?: User().apply {
                    firstName = newParent.firstName
                    lastName = newParent.lastName
                    email = newParent.email
                    phoneNumber = newParent.phoneNumber
                    role = UserRole.PARENT
                    school = student.user?.school
                    active = true
                }.also { it.persist() }

                val existingParent = Parent.find("user", parentUser).firstResult()
                val parentEntity = existingParent ?: Parent().apply {
                    user = parentUser
                    occupation = newParent.occupation
                    address = newParent.address ?: ""  // Handle potential null address
                    isActive = true
                }.also {
                    it.persist()
                    createdParents.add(it.id!!)
                }

                if (student.parents.add(parentEntity)) {
                    associatedParents.add(parentEntity.id!!)
                }

            } catch (e: Exception) {
                errors.add("Failed to create parent ${newParent.email}: ${e.message}")
            }
        }

        student.persist()

        return mapOf(
            "studentId" to student.id!!,
            "associatedParents" to associatedParents,
            "createdParents" to createdParents,
            "errors" to errors,
            "message" to "Successfully associated ${associatedParents.size} parents"
        )
    }
}
