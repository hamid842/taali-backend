package com.taali.application.service.school

import com.taali.api.dto.school.*
import com.taali.domain.model.school.School
import com.taali.domain.repository.school.SchoolRepository
import com.taali.domain.repository.user.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException

@ApplicationScoped
class SchoolService {

    @Inject
    lateinit var schoolRepository: SchoolRepository

    @Inject
    lateinit var userRepository: UserRepository

    fun getSchoolsByOwner(ownerId: String): List<SchoolDto> {
        return schoolRepository.findByOwnerId(ownerId).map { it.toDto() }
    }

    fun getSchoolById(id: Long): SchoolDto? {
        return schoolRepository.findById(id)?.toDto()
    }

    @Transactional
    fun createSchool(request: CreateSchoolRequest, ownerId: String): SchoolDto {
        // Check if school code already exists
        if (schoolRepository.existsByCode(request.code)) {
            throw IllegalArgumentException("School with code ${request.code} already exists")
        }

        val school = School().apply {
            name = request.name
            code = request.code
            image = request.image
            address = request.address
            email = request.email
            phone = request.phone
            this.ownerId = ownerId
        }
        schoolRepository.persist(school)
        return school.toDto()
    }

    @Transactional
    fun updateSchool(id: Long, request: UpdateSchoolRequest): SchoolDto {
        val school = schoolRepository.findById(id) ?: throw NotFoundException("School with id $id not found")

        request.name?.let { school.name = it }
        request.code?.let {
            if (schoolRepository.existsByCode(it) && school.code != it) {
                throw IllegalArgumentException("School with code $it already exists")
            }
            school.code = it
        }
        request.image?.let { school.image = it }
        request.address?.let { school.address = it }
        request.email?.let { school.email = it }
        request.phone?.let { school.phone = it }

        schoolRepository.persist(school)
        return school.toDto()
    }

    @Transactional
    fun deleteSchool(id: Long): Boolean {
        val school = schoolRepository.findById(id) ?: throw NotFoundException("School with id $id not found")

        // Check if school has users before deletion - FIXED: Use UserRepository
        val userCount = userRepository.countBySchoolId(id)
        if (userCount > 0) {
            throw IllegalStateException("Cannot delete school with existing users")
        }

        return schoolRepository.deleteById(id)
    }

    fun searchSchools(query: String): List<SchoolDto> {
        return schoolRepository.findByNameContainingIgnoreCase(query).map { it.toDto() }
    }

    @Transactional
    fun updateSchoolLogo(id: Long, imageUrl: String): SchoolDto {
        val school = schoolRepository.findById(id) ?: throw NotFoundException("School with id $id not found")

        // Validate the image URL format (basic validation)
        if (imageUrl.isBlank()) {
            throw IllegalArgumentException("Image URL cannot be empty")
        }

        // Optional: Add more URL validation if needed
        if (!isValidImageUrl(imageUrl)) {
            throw IllegalArgumentException("Invalid image URL format")
        }

        school.image = imageUrl
        schoolRepository.persist(school)
        return school.toDto()
    }

    private fun isValidImageUrl(url: String): Boolean {
        // Basic URL validation - you can enhance this based on your needs
        return try {
            // Check if it's a data URL (base64) or regular URL
            when {
                url.startsWith("data:image/") -> true // Base64 image data
                url.startsWith("/api/v1/uploads/schools/logos/") -> true // Our uploaded images
                url.startsWith("http://") || url.startsWith("https://") -> true // External URLs
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun School.toDto(): SchoolDto {
        return SchoolDto(
            id = id!!,
            name = name,
            code = code,
            image = image,
            address = address,
            email = email,
            phone = phone,
            ownerId = ownerId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
