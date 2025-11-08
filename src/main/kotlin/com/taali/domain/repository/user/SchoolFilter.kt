package com.taali.domain.repository.user

// Supporting sealed class for school filtering
sealed class SchoolFilter {
    data class OwnerSchoolsFilter(val schoolIds: List<Long>) : SchoolFilter()
    data class DirectFilter(val schoolId: Long) : SchoolFilter()
    object NoFilter : SchoolFilter()
}
