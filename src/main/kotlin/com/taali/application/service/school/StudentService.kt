package com.taali.application.service.school

import com.taali.domain.model.school.Student
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class StudentService {

    fun getStudentsBySchool(schoolId: Long): List<Student> {
        return Student.find("user.school.id", schoolId).list()
    }

    fun getStudentById(id: Long): Student? {
        return Student.findById(id)
    }

    fun getStudentsByClass(classId: Long): List<Student> {
        return Student.find("schoolClass.id", classId).list()
    }

    @Transactional
    fun createStudent(student: Student): Student {
        student.persist()
        return student
    }

    @Transactional
    fun updateStudent(id: Long, studentData: Map<String, Any>): Student? {
        val student = Student.findById(id) ?: return null

        studentData["studentId"]?.let { student.studentId = it.toString() }
        studentData["idNumber"]?.let { student.idNumber = it.toString() }
        studentData["birthDate"]?.let {
            // Handle date conversion if needed
        }
        studentData["gradeLevel"]?.let { student.gradeLevel = it.toString() }
        studentData["emergencyContact"]?.let { student.emergencyContact = it.toString() }
        studentData["emergencyPhone"]?.let { student.emergencyPhone = it.toString() }
        studentData["medicalNotes"]?.let { student.medicalNotes = it.toString() }

        return student
    }

    @Transactional
    fun deleteStudent(id: Long): Boolean {
        val student = Student.findById(id) ?: return false
        student.delete()
        return true
    }
}