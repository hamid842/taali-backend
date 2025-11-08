package com.taali.domain.repository.user

import com.taali.api.dto.user.UserPageDto
import com.taali.application.service.user.UserService
import com.taali.domain.model.user.User
import com.taali.domain.enum.UserRole
import com.taali.domain.enum.UserStatus
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.*
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

@ApplicationScoped
class UserRepository : PanacheRepository<User> {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    // Rename the field to avoid conflict with PanacheRepository's getEntityManager()
    @PersistenceContext
    lateinit var em: EntityManager  // Changed from entityManager to em

    fun findByEmail(email: String): User? {
        return find("email", email).firstResult()
    }

    fun findByPhoneNumber(phoneNumber: String): User? {
        return find("phoneNumber", phoneNumber).firstResult()
    }

    fun countBySchoolId(schoolId: Long): Long {
        return count("school.id", schoolId)
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

    // Add the missing findByUserId method
    fun findByUserId(userId: UUID): User? {
        return find("userId", userId).firstResult()
    }

    fun findByStatus(status: UserStatus): List<User> {
        return list("status", status)
    }

    fun findByRole(role: UserRole): List<User> {
        return list("role", role)
    }

    @Transactional
    fun updateStatus(userId: UUID, status: UserStatus): Boolean {
        val updated = update("status = ?1, updatedAt = ?2 where id = ?3", status, LocalDateTime.now(), userId)
        return updated > 0
    }

    @Transactional
    fun updateLastLogin(userId: UUID, lastLogin: LocalDateTime): Boolean {
        val updated = update(
            "lastLogin = ?1, updatedAt = ?2 where id = ?3", lastLogin, LocalDateTime.now(), userId
        )
        return updated > 0
    }

    fun searchUsers(query: String): List<User> {
        return list(
            """
            lower(firstName) like lower(?1) 
            or lower(lastName) like lower(?1) 
            or lower(email) like lower(?1)
        """.trimIndent(), "%$query%"
        )
    }

    fun findByEmailOrPhone(email: String, phoneNumber: String): User? {
        return find("email = ?1 or phoneNumber = ?2", email, phoneNumber).firstResult()
    }


    fun findActiveUsers(): List<User> {
        return list("status = 'ACTIVE'")
    }

    fun findUsersWithFilters(
        allowedRoles: List<UserRole>,
        schoolFilter: SchoolFilter,
        search: String?,
        role: UserRole?,
        page: Int,
        size: Int
    ): UserPageDto {
        val cb = em.criteriaBuilder
        val query: CriteriaQuery<User> = cb.createQuery(User::class.java)
        val root: Root<User> = query.from(User::class.java)

        val predicates = mutableListOf<Predicate>()

        // Role filter - only allowed roles
        val roleExpression = root.get<UserRole>("role")
        predicates.add(roleExpression.`in`(allowedRoles))

        // School filter
        when (schoolFilter) {
            is SchoolFilter.OwnerSchoolsFilter -> {
                if (schoolFilter.schoolIds.isNotEmpty()) {
                    val schoolJoin = root.join<Any, Any>("school")
                    predicates.add(schoolJoin.get<Long>("id").`in`(schoolFilter.schoolIds))
                } else {
                    // If no schools, return empty result
                    predicates.add(cb.equal(root.get<Long>("id"), -1L))
                }
            }
            is SchoolFilter.DirectFilter -> {
                predicates.add(cb.equal(root.get<Any>("school").get<Long>("id"), schoolFilter.schoolId))
            }
            is SchoolFilter.NoFilter -> {
                // No school filter applied - don't add any school predicates
                logger.debug("No school filter applied")
            }
        }

        // Specific role filter - use the same roleExpression
        if (role != null) {
            predicates.add(cb.equal(roleExpression, role))  // Use the same expression
        }

        // Search filter
        if (!search.isNullOrBlank()) {
            val searchPattern = "%$search%"
            val searchPredicate = cb.or(
                cb.like(cb.lower(root.get("firstName")), searchPattern.lowercase()),
                cb.like(cb.lower(root.get("lastName")), searchPattern.lowercase()),
                cb.like(cb.lower(root.get("email")), searchPattern.lowercase()),
                cb.like(root.get("phoneNumber"), searchPattern)
            )
            predicates.add(searchPredicate)
        }

        query.where(*predicates.toTypedArray())
        query.orderBy(cb.desc(root.get<LocalDateTime>("createdAt")))

        // Get total count
        val countQuery: CriteriaQuery<Long> = cb.createQuery(Long::class.java)
        val countRoot: Root<User> = countQuery.from(User::class.java)
        countQuery.select(cb.count(countRoot))

        // Build the same predicates for count query
        val countPredicates = mutableListOf<Predicate>()
        val countRoleExpression = countRoot.get<UserRole>("role")  // Use a new expression for count

        countPredicates.add(countRoleExpression.`in`(allowedRoles))

        // School filter for count
        when (schoolFilter) {
            is SchoolFilter.OwnerSchoolsFilter -> {
                if (schoolFilter.schoolIds.isNotEmpty()) {
                    val schoolJoin = countRoot.join<Any, Any>("school")
                    countPredicates.add(schoolJoin.get<Long>("id").`in`(schoolFilter.schoolIds))
                } else {
                    countPredicates.add(cb.equal(countRoot.get<Long>("id"), -1L))
                }
            }
            is SchoolFilter.DirectFilter -> {
                countPredicates.add(cb.equal(countRoot.get<Any>("school").get<Long>("id"), schoolFilter.schoolId))
            }

            else -> {}
        }

        // Specific role filter for count
        if (role != null) {
            countPredicates.add(cb.equal(countRoleExpression, role))
        }

        // Search filter for count
        if (!search.isNullOrBlank()) {
            val searchPattern = "%$search%"
            val searchPredicate = cb.or(
                cb.like(cb.lower(countRoot.get("firstName")), searchPattern.lowercase()),
                cb.like(cb.lower(countRoot.get("lastName")), searchPattern.lowercase()),
                cb.like(cb.lower(countRoot.get("email")), searchPattern.lowercase()),
                cb.like(countRoot.get("phoneNumber"), searchPattern)
            )
            countPredicates.add(searchPredicate)
        }

        countQuery.where(*countPredicates.toTypedArray())
        val totalCount = em.createQuery(countQuery).singleResult

        // Apply pagination
        val typedQuery = em.createQuery(query)
        val offset = (page - 1) * size
        typedQuery.firstResult = offset
        typedQuery.maxResults = size

        val users = typedQuery.resultList

        val totalPages = if (totalCount == 0L) 0 else ((totalCount - 1) / size + 1)

        return UserPageDto(
            users = users,
            currentPage = page,
            totalPages = totalPages,
            totalItems = totalCount,
            itemsPerPage = size
        )
    }

    // Find users by school with pagination
    fun findBySchoolId(schoolId: Long, page: Int, size: Int): UserPageDto {  // Changed from UUID to Long
        val cb = em.criteriaBuilder  // Changed from entityManager to em
        val query: CriteriaQuery<User> = cb.createQuery(User::class.java)
        val root: Root<User> = query.from(User::class.java)

        val predicates = mutableListOf<Predicate>()

        predicates.add(cb.equal(root.get<Any>("school").get<Long>("id"), schoolId))  // Changed from UUID to Long

        query.where(*predicates.toTypedArray())
        query.orderBy(cb.desc(root.get<LocalDateTime>("createdAt")))

        // Get total count
        val countQuery: CriteriaQuery<Long> = cb.createQuery(Long::class.java)
        val countRoot: Root<User> = countQuery.from(User::class.java)
        countQuery.select(cb.count(countRoot))
        countQuery.where(*predicates.toTypedArray())
        val totalCount = em.createQuery(countQuery).singleResult  // Changed from entityManager to em

        // Apply pagination
        val typedQuery = em.createQuery(query)  // Changed from entityManager to em
        val offset = (page - 1) * size
        typedQuery.firstResult = offset
        typedQuery.maxResults = size

        val users = typedQuery.resultList

        val totalPages = if (totalCount == 0L) 0L else ((totalCount - 1) / size + 1)

        return UserPageDto(
            users = users, currentPage = page, totalPages = totalPages, totalItems = totalCount, itemsPerPage = size
        )
    }

    // Find users by role and school with pagination
    fun findByRoleAndSchool(
        role: UserRole,
        schoolId: Long,
        page: Int,
        size: Int
    ): UserPageDto {  // Changed from UUID to Long
        val cb = em.criteriaBuilder  // Changed from entityManager to em
        val query: CriteriaQuery<User> = cb.createQuery(User::class.java)
        val root: Root<User> = query.from(User::class.java)

        val predicates = mutableListOf<Predicate>()

        predicates.add(cb.equal(root.get<UserRole>("role"), role))
        predicates.add(cb.equal(root.get<Any>("school").get<Long>("id"), schoolId))  // Changed from UUID to Long

        query.where(*predicates.toTypedArray())
        query.orderBy(cb.asc(root.get<String>("lastName")), cb.asc(root.get<String>("firstName")))

        // Get total count
        val countQuery: CriteriaQuery<Long> = cb.createQuery(Long::class.java)
        val countRoot: Root<User> = countQuery.from(User::class.java)
        countQuery.select(cb.count(countRoot))
        countQuery.where(*predicates.toTypedArray())
        val totalCount = em.createQuery(countQuery).singleResult  // Changed from entityManager to em

        // Apply pagination
        val typedQuery = em.createQuery(query)  // Changed from entityManager to em
        val offset = (page - 1) * size
        typedQuery.firstResult = offset
        typedQuery.maxResults = size

        val users = typedQuery.resultList

        val totalPages = if (totalCount == 0L) 0L else ((totalCount - 1) / size + 1)

        return UserPageDto(
            users = users, currentPage = page, totalPages = totalPages, totalItems = totalCount, itemsPerPage = size
        )
    }

    // Count users by role and school
    fun countByRoleAndSchool(role: UserRole, schoolId: Long): Long {  // Changed from UUID to Long
        return count("role = ?1 and school.id = ?2", role, schoolId)
    }

    // Find users created by a specific user (for audit)
    fun findByCreatedBy(createdBy: User, page: Int, size: Int): UserPageDto {  // Changed from UUID to User
        val users = find("createdBy", createdBy).page(page - 1, size).list()

        val totalCount = count("createdBy", createdBy)

        return UserPageDto(
            users = users,
            currentPage = page,
            totalPages = (totalCount + size - 1) / size,
            totalItems = totalCount,
            itemsPerPage = size
        )
    }
}

