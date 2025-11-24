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

    fun findByClassId(classId: Long): List<Teacher> {
        return find(
            """
        SELECT DISTINCT t 
        FROM Teacher t 
        JOIN t.classAssignments ct 
        JOIN ct.schoolClass c 
        WHERE c.id = ?1
        """,
            classId
        ).list()
    }

    fun findByStudentId(studentId: Long): List<Teacher> {
        return find(
            """
        SELECT DISTINCT t 
        FROM Teacher t 
        JOIN t.classAssignments ct
        JOIN ct.schoolClass c
        JOIN c.students s
        WHERE s.id = ?1
        """,
            studentId
        ).list()
    }

    fun findMainTeachersByStudentId(studentId: Long): List<Teacher> {
        return find(
            """SELECT DISTINCT t FROM Teacher t 
               JOIN SchoolClass sc ON sc.mainTeacher.id = t.id 
               JOIN sc.students s 
               WHERE s.id = ?1""",
            studentId
        ).list()
    }
}