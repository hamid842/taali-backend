package com.taali.domain.repository.school

import com.taali.domain.model.school.SchoolClass
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class SchoolClassRepository : PanacheRepository<SchoolClass> {

    fun findBySchool(schoolId: Long): List<SchoolClass> {
        return find("school.id", schoolId).list()
    }

    fun findBySchoolAndAcademicYear(schoolId: Long, academicYear: String): List<SchoolClass> {
        return find("school.id = ?1 and academicYear = ?2", schoolId, academicYear).list()
    }

    fun findByNameAndSchoolAndAcademicYear(name: String, schoolId: Long, academicYear: String): SchoolClass? {
        return find("name = ?1 and school.id = ?2 and academicYear = ?3", name, schoolId, academicYear).firstResult()
    }

    fun findActiveBySchool(schoolId: Long): List<SchoolClass> {
        return find("school.id = ?1 and isActive = ?2", schoolId, true).list()
    }
}