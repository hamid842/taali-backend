package com.taali.domain.service

import com.taali.domain.model.Student
import com.taali.infrastructure.persistence.repository.StudentRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class StudentService(
    private val studentRepository: StudentRepository
) {
    fun createStudent(student: Student): Unit = studentRepository.persist(student)
    fun getStudent(id: Long): Student? = studentRepository.findById(id)
    fun updateStudent(student: Student): Unit = studentRepository.persist(student)
    fun deleteStudent(id: Long) = studentRepository.deleteById(id)
    fun getAllStudents(): List<Student> = studentRepository.listAll()
}
