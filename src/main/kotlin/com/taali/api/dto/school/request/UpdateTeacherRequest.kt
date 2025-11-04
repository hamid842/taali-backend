package com.taali.api.dto.school.request

data class UpdateTeacherRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val specialization: String? = null,
    val qualification: String? = null,
    val schoolId: Long? = null,
    val classIds: List<Long>? = null
)