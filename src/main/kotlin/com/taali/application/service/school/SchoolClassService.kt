package com.taali.application.service.school

import com.taali.api.dto.school.request.CreateSchoolClassRequest
import com.taali.api.dto.school.request.UpdateSchoolClassRequest
import com.taali.api.dto.school.response.SchoolClassDetailResponse
import com.taali.api.dto.school.response.SchoolClassResponse
import com.taali.api.mapper.SchoolClassMapper
import com.taali.domain.model.school.*
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class SchoolClassService {

    @Transactional
    fun createClass(request: CreateSchoolClassRequest): SchoolClassResponse {
        // Use School.find() instead of School.findById()
        val foundSchool = School.find("id", request.schoolId).firstResult()
            ?: throw IllegalArgumentException("School not found with id: ${request.schoolId}")

        // Check if class name already exists for this school and academic year
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

        // Set main teacher if provided
        request.mainTeacherId?.let { teacherId ->
            val teacher = Teacher.find("id", teacherId).firstResult()
                ?: throw IllegalArgumentException("Teacher not found with id: $teacherId")
            schoolClass.mainTeacher = teacher
            schoolClass.teachers.add(teacher)
        }

        // Add students
        if (request.studentIds.isNotEmpty()) {
            val students = Student.list("id in ?1", request.studentIds)
            if (students.size != request.studentIds.size) {
                throw IllegalArgumentException("Some students not found")
            }
            schoolClass.students.addAll(students)
        }

        // Add additional teachers
        if (request.teacherIds.isNotEmpty()) {
            val teachers = Teacher.list("id in ?1", request.teacherIds)
            if (teachers.size != request.teacherIds.size) {
                throw IllegalArgumentException("Some teachers not found")
            }
            schoolClass.teachers.addAll(teachers)
        }

        schoolClass.persist()
        return SchoolClassMapper.toResponse(schoolClass)
    }

    fun getClassById(id: Long): SchoolClassDetailResponse {
        val schoolClass = SchoolClass.find("id", id).firstResult()
            ?: throw IllegalArgumentException("Class not found with id: $id")

        val schedules = ClassSchedule.find("schoolClass.id", id).list()
        return SchoolClassMapper.toDetailResponse(schoolClass, schedules)
    }

    fun getClassesBySchool(schoolId: Long): List<SchoolClassResponse> {
        return SchoolClass.find("school.id", schoolId).list().map {
            SchoolClassMapper.toResponse(it)
        }
    }

    fun getActiveClassesBySchool(schoolId: Long): List<SchoolClassResponse> {
        return SchoolClass.find("school.id = ?1 and isActive = ?2", schoolId, true).list().map {
            SchoolClassMapper.toResponse(it)
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

        // Update main teacher
        request.mainTeacherId?.let { teacherId ->
            val teacher = Teacher.find("id", teacherId).firstResult()
                ?: throw IllegalArgumentException("Teacher not found with id: $teacherId")
            schoolClass.mainTeacher = teacher
        }

        // Update students if provided
        request.studentIds?.let { studentIds ->
            val students = Student.list("id in ?1", studentIds)
            if (students.size != studentIds.size) {
                throw IllegalArgumentException("Some students not found")
            }
            schoolClass.students.clear()
            schoolClass.students.addAll(students)
        }

        // Update teachers if provided
        request.teacherIds?.let { teacherIds ->
            val teachers = Teacher.list("id in ?1", teacherIds)
            if (teachers.size != teacherIds.size) {
                throw IllegalArgumentException("Some teachers not found")
            }
            schoolClass.teachers.clear()
            schoolClass.teachers.addAll(teachers)
        }

        schoolClass.persist()
        return SchoolClassMapper.toResponse(schoolClass)
    }

    @Transactional
    fun addStudentToClass(classId: Long, studentId: Long): SchoolClassResponse {
        val schoolClass = SchoolClass.find("id", classId).firstResult()
            ?: throw IllegalArgumentException("Class not found with id: $classId")

        val student = Student.find("id", studentId).firstResult()
            ?: throw IllegalArgumentException("Student not found with id: $studentId")

        if (schoolClass.students.size >= schoolClass.capacity) {
            throw IllegalArgumentException("Class has reached maximum capacity")
        }

        schoolClass.students.add(student)
        schoolClass.persist()

        return SchoolClassMapper.toResponse(schoolClass)
    }

    @Transactional
    fun removeStudentFromClass(classId: Long, studentId: Long): SchoolClassResponse {
        val schoolClass = SchoolClass.find("id", classId).firstResult()
            ?: throw IllegalArgumentException("Class not found with id: $classId")

        val student = Student.find("id", studentId).firstResult()
            ?: throw IllegalArgumentException("Student not found with id: $studentId")

        schoolClass.students.remove(student)
        schoolClass.persist()

        return SchoolClassMapper.toResponse(schoolClass)
    }

    @Transactional
    fun deleteClass(id: Long) {
        val schoolClass = SchoolClass.find("id", id).firstResult()
            ?: throw IllegalArgumentException("Class not found with id: $id")

        // First delete schedules
        ClassSchedule.find("schoolClass.id", id).list().forEach { it.delete() }

        // Clear relationships before deletion
        schoolClass.students.clear()
        schoolClass.teachers.clear()
        schoolClass.persist()

        // Then delete the class
        schoolClass.delete()
    }
}