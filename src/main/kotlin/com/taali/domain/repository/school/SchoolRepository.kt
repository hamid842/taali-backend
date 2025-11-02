package com.taali.domain.repository.school

import com.taali.domain.model.school.School
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class SchoolRepository : PanacheRepository<School> {

    fun findByOwnerId(ownerId: String?): List<School> {
        return find("ownerId", ownerId as Any).list()
    }

    fun findByCode(code: String): School? {
        return find("code", code).firstResult()
    }

    fun existsByCode(code: String): Boolean {
        return count("code", code) > 0
    }

    fun findByNameContainingIgnoreCase(name: String): List<School> {
        return find("LOWER(name) LIKE LOWER(?1)", "%$name%").list()
    }

    fun findActiveSchools(): List<School> {
        return findAll(Sort.by("name")).list()
    }

    fun findByOwnerIdWithPagination(ownerId: Long, pageIndex: Int, pageSize: Int): List<School> {
        return find("ownerId", ownerId).page(pageIndex, pageSize).list()
    }

    fun countByOwnerId(ownerId: Long): Long {
        return count("ownerId", ownerId)
    }
}
