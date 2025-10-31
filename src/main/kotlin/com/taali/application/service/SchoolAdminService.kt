package com.taali.domain.service

import com.taali.domain.model.SchoolAdmin
import com.taali.infrastructure.persistence.repository.SchoolAdminRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class SchoolAdminService(
    private val schoolAdminRepository: SchoolAdminRepository
) {
    fun createSchoolAdmin(schoolAdmin: SchoolAdmin): Unit = schoolAdminRepository.persist(schoolAdmin)
    fun getSchoolAdmin(id: Long): SchoolAdmin? = schoolAdminRepository.findById(id)
    fun updateSchoolAdmin(schoolAdmin: SchoolAdmin): Unit = schoolAdminRepository.persist(schoolAdmin)
    fun deleteSchoolAdmin(id: Long) = schoolAdminRepository.deleteById(id)
    fun getAllSchoolAdmins(): List<SchoolAdmin> = schoolAdminRepository.listAll()
}


