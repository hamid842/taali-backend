package com.taali.application.service.school


import com.taali.api.dto.school.ClassroomDto
import com.taali.api.dto.school.CreateClassroomRequest
import com.taali.api.dto.school.UpdateClassroomRequest
import com.taali.domain.model.school.Classroom
import com.taali.domain.repository.school.ClassroomRepository
import com.taali.domain.repository.school.SchoolRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import java.time.LocalDateTime

@ApplicationScoped
class ClassroomService {

    @Inject
    lateinit var classroomRepository: ClassroomRepository

    @Inject
    lateinit var schoolRepository: SchoolRepository


    fun getClassroomsByUser(userId: String): List<ClassroomDto> {
        val classrooms = classroomRepository.findByUserId(userId)
        return classrooms.map { it.toDto() }
    }

    fun getClassroomById(id: Long): ClassroomDto? {
        return classroomRepository.findById(id)?.toDto()
    }

    @Transactional
    fun createClassroom(request: CreateClassroomRequest, userId: String): ClassroomDto {
        val school = schoolRepository.findById(request.schoolId)
            ?: throw NotFoundException("School with id ${request.schoolId} not found")

        if (classroomRepository.existsByNameAndSchoolId(request.name, request.schoolId)) {
            throw IllegalArgumentException("Classroom '${request.name}' already exists in this school")
        }

        val classroom = Classroom().apply {
            name = request.name
            grade = request.grade.toString()
            capacity = request.capacity!!
            this.school = school
            createdBy = userId
        }

        classroomRepository.persist(classroom)
        return classroom.toDto()
    }

    @Transactional
    fun updateClassroom(id: Long, request: UpdateClassroomRequest): ClassroomDto {
        val classroom = classroomRepository.findById(id)
            ?: throw NotFoundException("Classroom with id $id not found")

        request.name?.let {
            if (classroomRepository.existsByNameAndSchoolId(it, classroom.school.id!!) && classroom.name != it) {
                throw IllegalArgumentException("Classroom with name $it already exists in this school")
            }
            classroom.name = it
        }
        request.grade?.let { classroom.grade = it }
        request.capacity?.let { classroom.capacity = it }

        classroomRepository.persist(classroom)
        return classroom.toDto()
    }

    @Transactional
    fun deleteClassroom(id: Long): Boolean {
        val classroom = classroomRepository.findById(id)
            ?: throw NotFoundException("Classroom with id $id not found")

        if (classroomRepository.hasStudents(id)) {
            throw IllegalStateException("Cannot delete classroom with assigned students")
        }

        return classroomRepository.deleteById(id)
    }


    fun searchClassrooms(query: String): List<ClassroomDto> {
        return classroomRepository.findByNameContainingIgnoreCase(query).map { it.toDto() }
    }

    private fun Classroom.toDto(): ClassroomDto {
        return ClassroomDto(
            id = id!!,
            name = name,
            code = code,
            grade = grade,
            capacity = capacity,
            schoolId = school.id!!,
            teacherId = teacher?.id,
            createdAt = LocalDateTime.parse(createdAt),
            updatedAt = updatedAt?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now()
        )
    }
}
