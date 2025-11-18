package com.taali.application.service.school

import com.taali.api.dto.school.ClassTimestamp
import com.taali.api.dto.school.request.CreateClassTimestampRequest
import com.taali.api.dto.school.request.UpdateClassTimestampRequest
import com.taali.api.dto.school.response.ClassTimestampResponse
import com.taali.api.dto.school.response.AvailableTimestampTypesResponse
import com.taali.api.dto.school.response.TimestampTypeInfo
import com.taali.domain.enum.TimestampType
import com.taali.domain.model.school.School
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.LocalTime

@ApplicationScoped
class SchoolTimestampService {

    @Transactional
    fun getClassTimestamps(schoolId: Long): List<ClassTimestampResponse> {
        val school = School.findById(schoolId) ?: throw IllegalArgumentException("School not found with id: $schoolId")

        return ClassTimestamp.findBySchool(schoolId)
            .map { timestamp ->
                ClassTimestampResponse(
                    id = timestamp.id!!,
                    name = timestamp.name,
                    startTime = timestamp.startTime!!.toString(),
                    endTime = timestamp.endTime!!.toString(),
                    type = timestamp.type,
                    orderIndex = timestamp.orderIndex,
                    isActive = timestamp.isActive,
                    description = timestamp.description
                )
            }
    }

    @Transactional
    fun createClassTimestamp(schoolId: Long, request: CreateClassTimestampRequest): ClassTimestampResponse {
        val school = School.findById(schoolId) ?: throw IllegalArgumentException("School not found with id: $schoolId")

        // Validate time format and overlap
        validateTimestamp(schoolId, request.startTime, request.endTime)

        val timestamp = ClassTimestamp().apply {
            this.school = school
            this.name = request.name
            this.startTime = LocalTime.parse(request.startTime)
            this.endTime = LocalTime.parse(request.endTime)
            this.type = request.type
            this.orderIndex = request.orderIndex ?: getNextOrderIndex(schoolId)
            this.description = request.description
        }

        timestamp.persist()
        return ClassTimestampResponse(
            id = timestamp.id!!,
            name = timestamp.name,
            startTime = timestamp.startTime.toString(),
            endTime = timestamp.endTime.toString(),
            type = timestamp.type,
            orderIndex = timestamp.orderIndex,
            isActive = timestamp.isActive,
            description = timestamp.description
        )
    }

    @Transactional
    fun updateClassTimestamp(id: Long, request: UpdateClassTimestampRequest): ClassTimestampResponse {
        val timestamp = ClassTimestamp.findById(id) ?: throw IllegalArgumentException("Timestamp not found")

        request.name?.let { timestamp.name = it }
        request.startTime?.let { timestamp.startTime = LocalTime.parse(it) }
        request.endTime?.let { timestamp.endTime = LocalTime.parse(it) }
        request.type?.let { timestamp.type = it }
        request.orderIndex?.let { timestamp.orderIndex = it }
        request.isActive?.let { timestamp.isActive = it }
        request.description?.let { timestamp.description = it }

        timestamp.persist()

        return ClassTimestampResponse(
            id = timestamp.id!!,
            name = timestamp.name,
            startTime = timestamp.startTime.toString(),
            endTime = timestamp.endTime.toString(),
            type = timestamp.type,
            orderIndex = timestamp.orderIndex,
            isActive = timestamp.isActive,
            description = timestamp.description
        )
    }

    @Transactional
    fun deleteClassTimestamp(id: Long) {
        val timestamp = ClassTimestamp.findById(id) ?: throw IllegalArgumentException("Timestamp not found")
        timestamp.delete()
    }

    fun getAvailableTimestampTypes(schoolId: Long): AvailableTimestampTypesResponse {
        val school = School.findById(schoolId) ?: throw IllegalArgumentException("School not found with id: $schoolId")

        val types = mutableListOf<TimestampTypeInfo>()

        // Regular is always available
        types.add(
            TimestampTypeInfo(
                type = TimestampType.REGULAR,
                label = "Regular Class",
                enabled = true
            )
        )

        // Launch break available if school has cafeteria
        if (school.hasCafeteria) {
            types.add(
                TimestampTypeInfo(
                    type = TimestampType.LAUNCH,
                    label = "Launch Break",
                    enabled = true
                )
            )
        }

        // Pension break available if school has hostel facility
        if (school.hasHostelFacility) {
            types.add(
                TimestampTypeInfo(
                    type = TimestampType.PENSION,
                    label = "Pension Break",
                    enabled = true
                )
            )
        }

        return AvailableTimestampTypesResponse(types)
    }

    private fun validateTimestamp(schoolId: Long, startTime: String, endTime: String) {
        val start = LocalTime.parse(startTime)
        val end = LocalTime.parse(endTime)

        if (!end.isAfter(start)) {
            throw IllegalArgumentException("End time must be after start time")
        }

        // Check for overlaps
        val existing = ClassTimestamp.findBySchool(schoolId)
        existing.forEach { ts ->
            val tsStart = ts.startTime!!
            val tsEnd = ts.endTime!!

            if (start.isBefore(tsEnd) && end.isAfter(tsStart)) {
                throw IllegalArgumentException("Time slot overlaps with '${ts.name}' (${tsStart} - ${tsEnd})")
            }
        }
    }


    fun getAvailableTypes(schoolId: Long): AvailableTimestampTypesResponse {
        val school = School.findById(schoolId)
            ?: throw IllegalArgumentException("School not found")

        val types = mutableListOf<TimestampTypeInfo>()

        types.add(
            TimestampTypeInfo(
                type = TimestampType.REGULAR,
                label = "Regular Class",
                enabled = true
            )
        )

        if (school.hasCafeteria) {
            types.add(
                TimestampTypeInfo(
                    type = TimestampType.LAUNCH,
                    label = "Launch Break",
                    enabled = true
                )
            )
        }

        if (school.hasHostelFacility) {
            types.add(
                TimestampTypeInfo(
                    type = TimestampType.PENSION,
                    label = "Pension Break",
                    enabled = true
                )
            )
        }

        return AvailableTimestampTypesResponse(types)
    }

    private fun validateTimestamps(schoolId: Long, startTime: String, endTime: String) {
        val start = LocalTime.parse(startTime)
        val end = LocalTime.parse(endTime)

        if (!end.isAfter(start)) {
            throw IllegalArgumentException("End time must be after start time")
        }

        val existing = ClassTimestamp.findBySchool(schoolId)
        existing.forEach { ts ->
            val tsStart = ts.startTime!!
            val tsEnd = ts.endTime!!

            if (start.isBefore(tsEnd) && end.isAfter(tsStart)) {
                throw IllegalArgumentException("Time slot overlaps with '${ts.name}'")
            }
        }
    }

    private fun getNextOrderIndex(schoolId: Long): Int {
        return ClassTimestamp.find("school.id", schoolId).count().toInt()
    }

}