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

    // NEW: Find teachers by class ID through ClassTeacher relationship
    fun findByClassId(classId: Long): List<Teacher> {
        return find(
            "SELECT DISTINCT t FROM Teacher t JOIN t.classTeachers ct WHERE ct.schoolClass.id = ?1",
            classId
        ).list()
    }

    // NEW: Find teachers by student ID (through student's class)
    fun findByStudentId(studentId: Long): List<Teacher> {
        return find(
            """SELECT DISTINCT t FROM Teacher t 
               LEFT JOIN t.classTeachers ct 
               LEFT JOIN ct.schoolClass sc 
               LEFT JOIN sc.students s 
               WHERE s.id = ?1""",
            studentId
        ).list()
    }

    // NEW: Find main teachers by student ID (mainTeacher of student's class)
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