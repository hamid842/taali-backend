package com.taali.domain.repository.student

import com.taali.domain.model.school.Student
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class StudentRepository : PanacheRepository<Student> {

    fun findBySchool(schoolId: Long): List<Student> {
        return find("user.school.id", schoolId).list()
    }

    fun findActiveBySchool(schoolId: Long): List<Student> {
        return find("user.school.id = ?1 and isActive = ?2", schoolId, true).list()
    }

    fun findByClass(classId: Long): List<Student> {
        return find("schoolClass.id", classId).list()
    }

    fun findByUser(userId: Long): Student? {
        return find("user.id", userId).firstResult()
    }

    fun findByParent(parentId: Long): List<Student> {
        return find("parents.id", parentId).list()
    }

    fun searchBySchoolAndName(schoolId: Long, searchTerm: String): List<Student> {
        return find(
            "user.school.id = ?1 and (user.firstName like ?2 or user.lastName like ?2)",
            schoolId,
            "%$searchTerm%"
        ).list()
    }

    // NEW: Find student with class and teachers eagerly
    fun findByIdWithClassAndTeachers(studentId: Long): Student? {
        return find(
            "SELECT s FROM Student s LEFT JOIN FETCH s.schoolClass LEFT JOIN FETCH s.schoolClass.classTeachers WHERE s.id = ?1",
            studentId
        ).firstResult()
    }
}