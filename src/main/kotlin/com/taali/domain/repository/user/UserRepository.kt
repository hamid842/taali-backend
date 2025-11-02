package com.taali.domain.repository.user

import com.taali.domain.model.user.User
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.LocalDateTime
import java.util.UUID

@ApplicationScoped
class UserRepository : PanacheRepository<User> {

    fun findByEmail(email: String): User? {
        return find("email", email).firstResult()
    }

    fun findByPhoneNumber(phoneNumber: String): User? {
        return find("phoneNumber", phoneNumber).firstResult()
    }

    fun countBySchoolId(schoolId: Long): Long {
        return count("schoolId", schoolId)
    }

    fun existsByEmail(email: String): Boolean {
        return count("email", email) > 0
    }

    fun existsByPhoneNumber(phoneNumber: String): Boolean {
        return count("phoneNumber", phoneNumber) > 0
    }

    fun findById(id: UUID): User? {
        return find("id", id).firstResult()
    }

    fun findByStatus(status: String): List<User> {
        return list("status", status)
    }

    fun findByRole(role: String): List<User> {
        return list("role", role)
    }

    @Transactional
    fun updateStatus(userId: UUID, status: String): Boolean {
        val updated = update("status = ?1 where id = ?2", status, userId)
        return updated > 0
    }

    @Transactional
    fun updateLastLogin(userId: UUID, lastLogin: LocalDateTime): Boolean {
        val updated =
            update(
                "lastLogin = ?1, updatedAt = ?2 where id = ?3",
                lastLogin,
                LocalDateTime.now(),
                userId
            )
        return updated > 0
    }

    fun searchUsers(query: String): List<User> {
        return list(
            """
            lower(firstName) like lower(?1) 
            or lower(lastName) like lower(?1) 
            or lower(email) like lower(?1)
        """.trimIndent(),
            "%$query%"
        )
    }

    fun findByEmailOrPhone(email: String, phoneNumber: String): User? {
        return find("email = ?1 or phoneNumber = ?2", email, phoneNumber).firstResult()
    }

    @Transactional
    fun softDelete(userId: UUID): Boolean {
        val updated =
            update(
                "deleted = true, updatedAt = ?1 where id = ?2",
                LocalDateTime.now(),
                userId
            )
        return updated > 0
    }

    fun findActiveUsers(): List<User> {
        return list("deleted = false and status = 'ACTIVE'")
    }
}