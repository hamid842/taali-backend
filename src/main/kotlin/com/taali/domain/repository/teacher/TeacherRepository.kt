package com.taali.domain.repository.teacher

import com.taali.domain.model.school.Teacher
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TeacherRepository : PanacheRepository<Teacher> {

    fun findBySchool(schoolId: Long): List<Teacher> {
        return find("user.school.id", schoolId).list()
    }

    fun findActiveBySchool(schoolId: Long): List<Teacher> {
        return find("user.school.id = ?1 and isActive = ?2", schoolId, true).list()
    }

    fun findByEmail(email: String): Teacher? {
        return find("user.email", email).firstResult()
    }

    fun searchBySchoolAndName(schoolId: Long, searchTerm: String): List<Teacher> {
        return find(
            "user.school.id = ?1 and (user.firstName like ?2 or user.lastName like ?2 or user.email like ?2)",
            schoolId,
            "%$searchTerm%"
        ).list()
    }
}