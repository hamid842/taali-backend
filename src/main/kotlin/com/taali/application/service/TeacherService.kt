package com.taali.domain.service

import com.taali.domain.model.Teacher
import com.taali.infrastructure.persistence.repository.TeacherRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TeacherService(
    private val teacherRepository: TeacherRepository
) {
    fun createTeacher(teacher: Teacher): Unit = teacherRepository.persist(teacher)
    fun getTeacher(id: Long): Teacher? = teacherRepository.findById(id)
    fun updateTeacher(teacher: Teacher): Unit = teacherRepository.persist(teacher)
    fun deleteTeacher(id: Long) = teacherRepository.deleteById(id)
    fun getAllTeachers(): List<Teacher> = teacherRepository.listAll()
}
