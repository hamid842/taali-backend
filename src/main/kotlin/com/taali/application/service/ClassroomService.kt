package com.taali.domain.service

import com.taali.domain.model.Classroom
import com.taali.infrastructure.persistence.repository.ClassroomRepository
import com.taali.infrastructure.persistence.repository.StudentRepository
import com.taali.infrastructure.persistence.repository.TeacherRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ClassroomService(
    private val classroomRepository: ClassroomRepository,
    private val teacherRepository: TeacherRepository,
    private val studentRepository: StudentRepository
) {
    fun createClassroom(classroom: Classroom): Unit = classroomRepository.persist(classroom)
    fun getClassroom(id: Long): Classroom? = classroomRepository.findById(id)
    fun updateClassroom(classroom: Classroom): Unit = classroomRepository.persist(classroom)
    fun deleteClassroom(id: Long) = classroomRepository.deleteById(id)
    fun getAllClassrooms(): List<Classroom> = classroomRepository.listAll()

    fun assignTeacher(classroomId: Long, teacherId: Long) {
        val classroom = classroomRepository.findById(classroomId) ?: return
        val teacher = teacherRepository.findById(teacherId) ?: return
        classroom.teacher = teacher
        classroomRepository.persist(classroom)
    }

    fun assignStudent(classroomId: Long, studentId: Long) {
        val classroom = classroomRepository.findById(classroomId) ?: return
        val student = studentRepository.findById(studentId) ?: return
        classroom.students.add(student)
        classroomRepository.persist(classroom)
    }
}
