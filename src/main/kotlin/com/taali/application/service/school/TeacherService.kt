package com.taali.application.service.school

import com.taali.domain.model.school.Teacher
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class TeacherService {

    fun getTeachersBySchool(schoolId: Long): List<Teacher> {
        return Teacher.find("user.school.id", schoolId).list()
    }

    fun getTeacherById(id: Long): Teacher? {
        return Teacher.findById(id)
    }

    fun getTeacherClasses(teacherId: Long): List<Any> {
        val teacher = Teacher.findById(teacherId) ?: return emptyList()

        return teacher.classAssignments.map { assignment ->
            mapOf(
                "classId" to assignment.schoolClass?.id,
                "className" to assignment.schoolClass?.name,
                "subject" to assignment.subject,
                "isMainTeacher" to assignment.isMainTeacher
            )
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
        teacher.delete()
        return true
    }

    fun getActiveTeachersBySchool(schoolId: Long): List<Teacher> {
        return Teacher.find("user.school.id = ?1 and isActive = ?2", schoolId, true).list()
    }
}