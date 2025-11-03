package com.taali.domain.repository.school

import com.taali.domain.model.school.Classroom
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ClassroomRepository : PanacheRepository<Classroom> {

    fun findByUserId(userId: String): List<Classroom> {
        return find("createdBy", userId).list()
    }

    fun findByNameContainingIgnoreCase(query: String): List<Classroom> {
        return find("LOWER(name) LIKE LOWER(?1)", "%$query%").list()
    }

    fun existsByNameAndSchoolId(name: String, schoolId: Long): Boolean {
        return count("LOWER(name) = LOWER(?1) AND school.id = ?2", name, schoolId) > 0
    }

    fun hasStudents(classroomId: Long): Boolean {
        val classroom = findById(classroomId)
        return classroom?.students?.isNotEmpty() ?: false
    }

    override fun deleteById(id: Long): Boolean {
        return delete("id", id) > 0
    }

    fun findBySchoolId(schoolId: Long): List<Classroom> {
        return find("school.id", schoolId).list()
    }

    fun findAllSorted(): List<Classroom> {
        return findAll(Sort.by("name")).list()
    }
}
