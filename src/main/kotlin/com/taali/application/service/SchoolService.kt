package com.taali.domain.service

import com.taali.domain.model.School
import com.taali.infrastructure.persistence.repository.SchoolRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class SchoolService(
    private val schoolRepository: SchoolRepository
) {
    fun createSchool(school: School): Unit = schoolRepository.persist(school)
    fun getSchool(id: Long): School? = schoolRepository.findById(id)
    fun updateSchool(school: School): Unit = schoolRepository.persist(school)
    fun deleteSchool(id: Long) = schoolRepository.deleteById(id)
    fun getAllSchools(): List<School> = schoolRepository.listAll()
}
