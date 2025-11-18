package com.taali.application.service.school

import com.taali.api.dto.school.request.CreateSchoolClassRequest
import com.taali.api.dto.school.request.UpdateSchoolClassRequest
import com.taali.api.dto.school.response.SchoolClassDetailResponse
import com.taali.api.dto.school.response.SchoolClassResponse
import com.taali.api.mapper.SchoolClassMapper
import com.taali.domain.model.school.*
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@ApplicationScoped
class SchoolClassService {

    @Transactional
    fun createClass(request: CreateSchoolClassRequest): SchoolClassResponse {
        val foundSchool = School.find("id", request.schoolId).firstResult()
            ?: throw IllegalArgumentException("School not found with id: ${request.schoolId}")

        val existingClass = SchoolClass.find(
            "name = ?1 and school.id = ?2 and academicYear = ?3",
            request.name, request.schoolId, request.academicYear
        ).firstResult()

        if (existingClass != null) {
            throw IllegalArgumentException("Class with name '${request.name}' already exists for this academic year")
        }

        val schoolClass = SchoolClass().apply {
            name = request.name
            gradeLevel = request.gradeLevel
            academicYear = request.academicYear
            capacity = request.capacity
            school = foundSchool
        }

        schoolClass.persist()

        if (request.mainTeacherId != null || request.teacherIds.isNotEmpty()) {
            assignTeachersToClass(schoolClass.id!!, request.teacherIds, request.mainTeacherId)
        }

        // Add students via Student.schoolClass
        if (request.studentIds.isNotEmpty()) {
            val students = Student.list("id in ?1", request.studentIds)
            if (students.size != request.studentIds.size) {
                throw IllegalArgumentException("Some students not found")
            }
            students.forEach { student ->
                student.schoolClass = schoolClass
                student.persist()
            }
        }

        val studentCount = Student.count("schoolClass.id", schoolClass.id!!)
        return SchoolClassMapper.toResponse(schoolClass, studentCount)
    }

    @Transactional
    fun assignTeachersToClass(classId: Long, teacherIds: List<Long>, mainTeacherId: Long?) {
        val schoolClass = SchoolClass.findById(classId)
            ?: throw IllegalArgumentException("Class not found with id: $classId")

        mainTeacherId?.let { teacherId ->
            val teacher = Teacher.findById(teacherId)
                ?: throw IllegalArgumentException("Teacher not found with id: $teacherId")

            val mainClassTeacher = ClassTeacher().apply {
                this.teacher = teacher
                this.schoolClass = schoolClass
                this.subject = teacher.specializations.firstOrNull() ?: "General"
                this.isMainTeacher = true
                // createdAt/updatedAt handled by AuditableEntity
            }
            mainClassTeacher.persist()
        }

        teacherIds.forEach { teacherId ->
            if (teacherId != mainTeacherId) {
                val teacher = Teacher.findById(teacherId)
                    ?: throw IllegalArgumentException("Teacher not found with id: $teacherId")

                val classTeacher = ClassTeacher().apply {
                    this.teacher = teacher
                    this.schoolClass = schoolClass
                    this.subject = teacher.specializations.firstOrNull() ?: "General"
                    this.isMainTeacher = false
                    // createdAt/updatedAt handled by AuditableEntity
                }
                classTeacher.persist()
            }
        }
    }

    @Transactional
    fun getClassById(id: Long): SchoolClassDetailResponse {
        val schoolClass = SchoolClass.find("id", id).firstResult()
            ?: throw IllegalArgumentException("Class not found with id: $id")

        val students = Student.list("schoolClass.id", id)
        val schedules = ClassSchedule.find("schoolClass.id", id).list()
        return SchoolClassMapper.toDetailResponse(schoolClass, students, schedules)
    }

    @Transactional
    fun getClassesBySchool(schoolId: Long): List<SchoolClassResponse> {
        val classes = SchoolClass.find(
            """select distinct sc from SchoolClass sc 
           left join fetch sc.mainTeacher mt
           left join fetch mt.user u
           left join fetch sc.school s
           where sc.school.id = ?1""",
            schoolId
        ).list()

        val classIds = classes.mapNotNull { it.id }
        val studentCounts = if (classIds.isNotEmpty()) {
            Student.find(
                "select schoolClass.id, count(*) from Student where schoolClass.id in ?1 group by schoolClass.id",
                classIds
            ).list() as List<Array<Any>>
        } else {
            emptyList()
        }

        val countMap = studentCounts.associate {
            (it[0] as Long) to (it[1] as Long)
        }

        return classes.map { sc ->
            SchoolClassMapper.toResponse(sc, countMap[sc.id] ?: 0)
        }
    }

    @Transactional
    fun getActiveClassesBySchool(schoolId: Long): List<SchoolClassResponse> {
        return SchoolClass.find("school.id = ?1 and isActive = ?2", schoolId, true).list().map {
            val count = Student.count("schoolClass.id", it.id!!)
            SchoolClassMapper.toResponse(it, count)
        }
    }

    @Transactional
    fun updateClass(id: Long, request: UpdateSchoolClassRequest): SchoolClassResponse {
        val schoolClass = SchoolClass.find("id", id).firstResult()
            ?: throw IllegalArgumentException("Class not found with id: $id")

        request.name?.let { schoolClass.name = it }
        request.gradeLevel?.let { schoolClass.gradeLevel = it }
        request.academicYear?.let { schoolClass.academicYear = it }
        request.capacity?.let { schoolClass.capacity = it }
        request.isActive?.let { schoolClass.isActive = it }

        request.mainTeacherId?.let { teacherId ->
            val teacher = Teacher.find("id", teacherId).firstResult()
                ?: throw IllegalArgumentException("Teacher not found with id: $teacherId")
            schoolClass.mainTeacher = teacher
        }

        // Update students
        request.studentIds?.let { studentIds ->
            Student.update("schoolClass = null where schoolClass.id = ?1", id)
            if (studentIds.isNotEmpty()) {
                val updatedCount = Student.update(
                    "schoolClass = ?1 where id in ?2",
                    schoolClass,
                    studentIds
                )
                if (updatedCount.toLong() != studentIds.size.toLong()) {
                    throw IllegalArgumentException("Some students not found")
                }
            }
        }

        // Update teachers
        request.teacherIds?.let { teacherIds ->
            ClassTeacher.delete("schoolClass.id = ?1", id)
            if (teacherIds.isNotEmpty()) {
                assignTeachersToClass(id, teacherIds, request.mainTeacherId)
            }
        }

        schoolClass.persist()
        val count = Student.count("schoolClass.id", id)
        return SchoolClassMapper.toResponse(schoolClass, count)
    }

    @Transactional
    fun addStudentToClass(classId: Long, studentId: Long): SchoolClassResponse {
        val schoolClass = SchoolClass.find("id", classId).firstResult()
            ?: throw IllegalArgumentException("Class not found with id: $classId")

        val student = Student.find("id", studentId).firstResult()
            ?: throw IllegalArgumentException("Student not found with id: $studentId")

        val currentCount = Student.count("schoolClass.id", classId)
        if (currentCount >= schoolClass.capacity) {
            throw IllegalArgumentException("Class has reached maximum capacity")
        }

        student.schoolClass = schoolClass
        student.persist()

        val newCount = Student.count("schoolClass.id", classId)
        return SchoolClassMapper.toResponse(schoolClass, newCount)
    }

    @Transactional
    fun removeStudentFromClass(classId: Long, studentId: Long): SchoolClassResponse {
        val schoolClass = SchoolClass.find("id", classId).firstResult()
            ?: throw IllegalArgumentException("Class not found with id: $classId")

        val student = Student.find("id", studentId).firstResult()
            ?: throw IllegalArgumentException("Student not found with id: $studentId")

        student.schoolClass = null
        student.persist()

        val newCount = Student.count("schoolClass.id", classId)
        return SchoolClassMapper.toResponse(schoolClass, newCount)
    }

    @Transactional
    fun deleteClass(id: Long) {
        val schoolClass = SchoolClass.find("id", id).firstResult()
            ?: throw IllegalArgumentException("Class not found with id: $id")

        ClassSchedule.delete("schoolClass.id", id)
        Student.update("schoolClass = null where schoolClass.id = ?1", id)
        ClassTeacher.delete("schoolClass.id", id)
        schoolClass.delete()
    }
}